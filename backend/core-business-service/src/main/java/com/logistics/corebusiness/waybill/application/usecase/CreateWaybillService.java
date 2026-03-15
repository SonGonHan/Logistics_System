package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.adapter.in.WaybillControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.exception.DraftInvalidStatusException;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.exception.WaybillValidationException;
import com.logistics.corebusiness.waybill.application.port.in.CreateWaybillUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.CreateWaybillCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillStatusHistoryRepository;
import com.logistics.corebusiness.waybill.application.util.WaybillNumberGenerator;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import com.logistics.shared.pricing_rule.PricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Сервис создания подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Создает накладную из черновика или напрямую без черновика.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateWaybillService implements CreateWaybillUseCase {

    private final WaybillRepository waybillRepository;
    private final DraftRepository draftRepository;
    private final WaybillStatusHistoryRepository historyRepository;
    private final WaybillNumberGenerator waybillNumberGenerator;
    private final PricingRuleService pricingRuleService;
    private final CreateAuditLogUseCase auditLogUseCase;

    @Override
    public WaybillResponse create(CreateWaybillCommand command) {
        var waybillNumber = waybillNumberGenerator.generate();
        var now = LocalDateTime.now();

        Draft draft = null;
        Waybill waybill;

        if (command.draftId() != null) {
            draft = draftRepository.findById(command.draftId())
                    .orElseThrow(() -> DraftNotFoundException.byId(command.draftId()));

            if (draft.getDraftStatus() != DraftStatus.PENDING) {
                throw DraftInvalidStatusException.forOperation("finalize", draft.getDraftStatus());
            }

            var finalPrice = pricingRuleService.calculatePrice(draft.getPricingRuleId());
            waybill = Waybill.builder()
                    .waybillNumber(waybillNumber)
                    .waybillCreatorId(command.waybillCreatorId())
                    .senderUserId(draft.getSenderUserId())
                    .recipientUserId(draft.getRecipientUserId())
                    .recipientAddress(draft.getRecipientAddress())
                    .pricingRuleId(draft.getPricingRuleId())
                    .finalPrice(finalPrice)
                    .status(WaybillStatus.ACCEPTED_AT_PVZ)
                    .createdAt(now)
                    .acceptedAt(now)
                    .build();
        } else {
            validateCreateWithoutDraft(command);

            var finalPrice = pricingRuleService.calculatePrice(command.pricingRuleId());
            waybill = Waybill.builder()
                    .waybillNumber(waybillNumber)
                    .waybillCreatorId(command.waybillCreatorId())
                    .senderUserId(command.senderUserId())
                    .recipientUserId(command.recipientUserId())
                    .recipientAddress(command.recipientAddress())
                    .pricingRuleId(command.pricingRuleId())
                    .finalPrice(finalPrice)
                    .status(WaybillStatus.ACCEPTED_AT_PVZ)
                    .createdAt(now)
                    .acceptedAt(now)
                    .build();
        }

        var savedWaybill = waybillRepository.save(waybill);

        var historyEntry = WaybillStatusHistory.builder()
                .waybillId(savedWaybill.getId())
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .changedBy(command.waybillCreatorId())
                .changedAt(now)
                .build();
        historyRepository.save(historyEntry);

        if (draft != null) {
            draft.setDraftStatus(DraftStatus.CONFIRMED);
            draftRepository.save(draft);

            auditLogUseCase.create(new CreateAuditLogCommand(
                    command.waybillCreatorId(),
                    "WAYBILL_FINALIZE",
                    null,
                    null,
                    Map.of("draftId", command.draftId()),
                    "waybills",
                    savedWaybill.getId()
            ));
        } else {
            auditLogUseCase.create(new CreateAuditLogCommand(
                    command.waybillCreatorId(),
                    "WAYBILL_CREATE",
                    null,
                    null,
                    null,
                    "waybills",
                    savedWaybill.getId()
            ));
        }

        return WaybillControllerMapper.toResponse(savedWaybill);
    }

    private void validateCreateWithoutDraft(CreateWaybillCommand command) {
        if (command.senderUserId() == null
                || command.recipientUserId() == null
                || command.recipientAddress() == null
                || command.recipientAddress().isBlank()
                || command.pricingRuleId() == null) {
            throw new WaybillValidationException(
                    "senderUserId, recipientUserId, recipientAddress, pricingRuleId are required when draftId is null");
        }
    }
}
