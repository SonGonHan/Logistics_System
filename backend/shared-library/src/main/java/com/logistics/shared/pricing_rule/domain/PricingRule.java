package com.logistics.shared.pricing_rule.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Доменная модель правила ценообразования.
 *
 * <h2>Назначение</h2>
 * Представляет правило расчета стоимости доставки груза в зависимости от:
 * - Зоны доставки (город, межгород, международная)
 * - Весовой категории груза (от weightMin до weightMax)
 * - Периода действия правила (effectiveFrom — effectiveTo)
 *
 * <h2>Формула расчета</h2>
 * Итоговая стоимость = basePrice + (pricePerKg × вес груза)
 *
 * @see DeliveryZone для зон доставки
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PricingRule {

    private Long id;

    private String ruleName;

    private DeliveryZone deliveryZone;

    private BigDecimal weightMin;

    private BigDecimal weightMax;

    private BigDecimal basePrice;

    private BigDecimal pricePerKg;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) return false;
        if (effectiveTo != null && now.isAfter(effectiveTo)) return false;
        return true;
    }

    public boolean isWeightSuitable(BigDecimal weight) {
        if (weightMin != null && weight.compareTo(weightMin) < 0) return false;
        if (weightMax != null && weight.compareTo(weightMax) > 0) return false;
        return true;
    }

    public boolean isSuitable(BigDecimal weight, DeliveryZone deliveryZone) {
        return this.isActive()
                && this.isWeightSuitable(weight)
                && this.deliveryZone == deliveryZone;
    }
}
