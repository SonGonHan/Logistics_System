package com.logistics.corebusiness.waybill.application.port.in.command;

import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.shared.pricing_rule.domain.PricingRule;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Команда на создание черновика накладной.
 *
 * <h2>Обогащение данных</h2>
 * - draftCreatorId: Извлекается из Spring Security Authentication
 * - senderUserId: По умолчанию равен draftCreatorId (клиент отправляет сам себе)
 * - barcode: Генерируется в Service
 * - estimatedPrice: Рассчитывается в Service на основе pricing rules
 */
@Builder
public record CreateDraftCommand(
        Long draftCreatorId,
        Long senderUserId,
        Long recipientUserId,
        String recipientAddress,
        Long pricingRuleId,
        BigDecimal weightDeclared,
        Dimensions dimensions
) {
}
