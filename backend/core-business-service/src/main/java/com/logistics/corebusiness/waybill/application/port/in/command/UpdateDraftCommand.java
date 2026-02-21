package com.logistics.corebusiness.waybill.application.port.in.command;

import com.logistics.corebusiness.waybill.domain.Dimensions;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Команда на обновление черновика накладной.
 *
 * <h2>Обогащение данных</h2>
 * - userId: Извлекается из Spring Security Authentication (для проверки владения)
 * - estimatedPrice: Пересчитывается если изменились вес/габариты
 */
@Builder
public record UpdateDraftCommand(
        Long draftId,
        Long userId,
        Long recipientUserId,
        String recipientAddress,
        BigDecimal weightDeclared,
        Dimensions dimensions,
        Long pricingRuleId
) {
}
