package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.DraftControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.UpdateDraftUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.shared.pricing_rule.PricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис обновления черновика накладной.
 *
 * <h2>Бизнес-логика</h2>
 * - Обновляет только переданные поля (null = не изменять)
 * - Пересчитывает estimatedPrice если изменился тарифный план
 *
 * <h2>Ограничения</h2>
 * Нельзя изменить CONFIRMED или CANCELLED черновик.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateDraftService implements UpdateDraftUseCase {

    private final DraftRepository repository;
    private final PricingRuleService pricingRuleService;

    @Override
    public DraftResponse update(UpdateDraftCommand command) {
        var draft = repository.findById(command.draftId())
                .orElseThrow(() -> DraftNotFoundException.byId(command.draftId()));

        updateFields(draft, command);

        var updated = repository.save(draft);
        return DraftControllerMapper.toResponse(updated);
    }

    private void updateFields(Draft draft, UpdateDraftCommand command) {
        if (command.recipientUserId() != null) {
            draft.setRecipientUserId(command.recipientUserId());
        }

        if (command.recipientAddress() != null) {
            draft.setRecipientAddress(command.recipientAddress());
        }

        if (command.pricingRuleId() != null) {
            draft.setPricingRuleId(command.pricingRuleId());
            draft.setEstimatedPrice(pricingRuleService.calculatePrice(command.pricingRuleId()));
        }
    }

}