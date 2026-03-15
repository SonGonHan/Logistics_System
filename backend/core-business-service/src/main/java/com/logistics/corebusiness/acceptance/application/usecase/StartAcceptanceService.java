package com.logistics.corebusiness.acceptance.application.usecase;

import com.logistics.corebusiness.acceptance.adapter.in.web.dto.AcceptanceResponse;
import com.logistics.corebusiness.acceptance.application.exception.AcceptanceInvalidStatusException;
import com.logistics.corebusiness.acceptance.application.exception.AcceptanceNotFoundException;
import com.logistics.corebusiness.acceptance.application.exception.AcceptanceValidationException;
import com.logistics.corebusiness.acceptance.application.port.in.StartAcceptanceUseCase;
import com.logistics.corebusiness.acceptance.application.port.in.command.StartAcceptanceCommand;
import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.shared.company_facilities.FacilityService;
import com.logistics.shared.company_facilities.domain.FacilityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Сервис начала приёмки посылки на ПВЗ.
 *
 * <h2>Бизнес-логика</h2>
 * - Ищет черновик по barcode
 * - Разрешает операцию только для черновиков со статусом PENDING
 * - Проверяет, что указанный объект является активным ПВЗ
 * - Пишет audit log о начале приёмки
 * - Возвращает данные черновика для проверки оператором
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StartAcceptanceService implements StartAcceptanceUseCase {

    private final DraftRepository draftRepository;
    private final FacilityService facilityService;
    private final CreateAuditLogUseCase auditLogUseCase;

    /**
     * Начинает процесс приёмки по штрих-коду черновика.
     *
     * @param command входные данные операции
     * @return данные найденного черновика
     */
    @Override
    public AcceptanceResponse start(StartAcceptanceCommand command) {
        var draft = draftRepository.findByBarcode(command.barcode())
                .orElseThrow(() -> AcceptanceNotFoundException.byBarcode(command.barcode()));

        if (draft.getDraftStatus() != DraftStatus.PENDING) {
            throw AcceptanceInvalidStatusException.notPending(command.barcode(), draft.getDraftStatus());
        }

        validateFacility(command.facilityId());

        auditLogUseCase.create(new CreateAuditLogCommand(
                command.operatorId(),
                "PVZ_ACCEPTANCE_START",
                null,
                null,
                Map.of(
                        "barcode", command.barcode(),
                        "facilityId", command.facilityId()
                ),
                "waybill_drafts",
                draft.getId()
        ));

        return AcceptanceResponse.builder()
                .draftId(draft.getId())
                .barcode(draft.getBarcode())
                .senderUserId(draft.getSenderUserId())
                .recipientUserId(draft.getRecipientUserId())
                .recipientAddress(draft.getRecipientAddress())
                .estimatedPrice(draft.getEstimatedPrice())
                .draftStatus(draft.getDraftStatus().name())
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
