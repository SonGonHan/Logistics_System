package com.logistics.corebusiness.waybill.application.port.in.command;

import lombok.Builder;

/**
 * Команда на обновление черновика накладной.
 *
 * <h2>Обогащение данных</h2>
 * - userId: Извлекается из Spring Security Authentication (для проверки владения)
 * - estimatedPrice: Пересчитывается если изменился тарифный план (pricingRuleId)
 */
@Builder
public record UpdateDraftCommand(
        Long draftId,
        Long userId,
        Long recipientUserId,
        String recipientAddress,
        Long pricingRuleId
) {
}