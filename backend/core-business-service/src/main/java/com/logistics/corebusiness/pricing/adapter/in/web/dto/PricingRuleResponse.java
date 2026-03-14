package com.logistics.corebusiness.pricing.adapter.in.web.dto;

import com.logistics.shared.pricing_rule.domain.DeliveryZone;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO для передачи тарифного правила клиентам.
 */
@Builder
public record PricingRuleResponse(
        Long id,
        String ruleName,
        DeliveryZone deliveryZone,
        BigDecimal weightMin,
        BigDecimal weightMax,
        BigDecimal basePrice
) {
}