package com.logistics.corebusiness.acceptance.application.usecase;

import com.logistics.corebusiness.acceptance.adapter.in.web.dto.CompleteAcceptanceResponse;
import com.logistics.corebusiness.acceptance.application.exception.AcceptanceInvalidStatusException;
import com.logistics.corebusiness.acceptance.application.exception.AcceptanceNotFoundException;
import com.logistics.corebusiness.acceptance.application.exception.AcceptanceValidationException;
import com.logistics.corebusiness.acceptance.application.port.in.CompleteAcceptanceUseCase;
import com.logistics.corebusiness.acceptance.application.port.in.command.CompleteAcceptanceCommand;
import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillStatusHistoryRepository;
import com.logistics.corebusiness.waybill.application.util.WaybillNumberGenerator;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import com.logistics.shared.company_facilities.FacilityService;
import com.logistics.shared.company_facilities.domain.FacilityType;
import com.logistics.shared.pricing_rule.PricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Сервис завершения приёмки посылки на ПВЗ.
 *
 * <h2>Бизнес-логика</h2>
 * - Ищет черновик по ID
 * - Разрешает завершение только для статуса PENDING
 * - Проверяет, что операция выполняется на активном ПВЗ
 * - Рассчитывает итоговую цену и создаёт накладную
 * - Создаёт первую запись в истории статусов
 * - Переводит черновик в статус CONFIRMED
 * - Пишет audit log о создании накладной
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CompleteAcceptanceService implements CompleteAcceptanceUseCase {

    private final DraftRepository draftRepository;
    private final WaybillRepository waybillRepository;
    private final WaybillStatusHistoryRepository historyRepository;
    private final PricingRuleService pricingRuleService;
    private final FacilityService facilityService;
    private final WaybillNumberGenerator waybillNumberGenerator;
    private final CreateAuditLogUseCase auditLogUseCase;

    /**
     * Завершает приёмку и создаёт накладную из черновика.
     *
     * @param command входные данные операции
     * @return данные созданной накладной
     */
    @Override
    public CompleteAcceptanceResponse complete(CompleteAcceptanceCommand command) {
        var draft = draftRepository.findById(command.draftId())
                .orElseThrow(() -> AcceptanceNotFoundException.byId(command.draftId()));

        if (draft.getDraftStatus() != DraftStatus.PENDING) {
            throw AcceptanceInvalidStatusException.notPending(draft.getBarcode(), draft.getDraftStatus());
        }

        validateFacility(command.facilityId());

        var finalPrice = pricingRuleService.calculatePrice(draft.getPricingRuleId());
        var waybillNumber = waybillNumberGenerator.generate();
        var now = LocalDateTime.now();

        var waybill = Waybill.builder()
                .waybillNumber(waybillNumber)
                .waybillCreatorId(command.operatorId())
                .senderUserId(draft.getSenderUserId())
                .recipientUserId(draft.getRecipientUserId())
                .recipientAddress(draft.getRecipientAddress())
                .pricingRuleId(draft.getPricingRuleId())
                .finalPrice(finalPrice)
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .createdAt(now)
                .acceptedAt(now)
                .build();
        var savedWaybill = waybillRepository.save(waybill);

        var history = WaybillStatusHistory.builder()
                .waybillId(savedWaybill.getId())
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .facilityId(command.facilityId())
                .notes("Посылка принята на ПВЗ")
                .changedBy(command.operatorId())
                .changedAt(now)
                .build();
        historyRepository.save(history);

        draft.setDraftStatus(DraftStatus.CONFIRMED);
        draftRepository.save(draft);

        auditLogUseCase.create(new CreateAuditLogCommand(
                command.operatorId(),
                "WAYBILL_CREATE",
                null,
                null,
                Map.of(
                        "waybillNumber", waybillNumber,
                        "draftId", command.draftId()
                ),
                "waybills",
                savedWaybill.getId()
        ));

        return CompleteAcceptanceResponse.builder()
                .waybillId(savedWaybill.getId())
                .waybillNumber(savedWaybill.getWaybillNumber())
                .finalPrice(savedWaybill.getFinalPrice())
                .waybillStatus(savedWaybill.getStatus().name())
                .acceptedAt(savedWaybill.getAcceptedAt())
                .build();
    }

    /**
     * Проверяет, что объект существует, относится к ПВЗ и активен.
     *
     * @param facilityId идентификатор объекта
     */
    private void validateFacility(Long facilityId) {
        var facility = facilityService.getById(facilityId)
                .orElseThrow(() -> new AcceptanceValidationException("Facility not found with id: " + facilityId));

        if (facility.getType() != FacilityType.PVZ) {
            throw new AcceptanceValidationException("Facility with id " + facilityId + " is not a PVZ");
        }

        if (!facilityService.isActive(facility)) {
            throw new AcceptanceValidationException("Facility with id " + facilityId + " is not active");
        }
    }
}
