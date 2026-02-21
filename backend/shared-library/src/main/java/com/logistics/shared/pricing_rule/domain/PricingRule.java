package com.logistics.shared.pricing_rule.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Доменная модель правила ценообразования.
 *
 * <h2>Назначение</h2>
 * Представляет тарифный план доставки, определяющий:
 * - Зону доставки (город, межгород, региональная)
 * - Предельный вес груза (weightMax)
 * - Предельные габариты (maxDimensions)
 * - Период действия правила (effectiveFrom — effectiveTo)
 *
 * <h2>Формула расчета</h2>
 * Итоговая стоимость = basePrice (фиксированная ставка за тарифный план)
 *
 * <h2>Подбор тарифа</h2>
 * Выбирается правило с наименьшим weightMax >= фактическому весу,
 * соответствующее зоне доставки.
 *
 * @see DeliveryZone для зон доставки
 * @see Dimensions для габаритов
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PricingRule {

    private Long id;

    private String ruleName;

    private DeliveryZone deliveryZone;

    private BigDecimal weightMax;

    private Dimensions maxDimensions;

    private BigDecimal basePrice;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        if (effectiveFrom != null && now.isBefore(effectiveFrom)) return false;
        if (effectiveTo != null && now.isAfter(effectiveTo)) return false;
        return true;
    }

    public boolean isWeightSuitable(BigDecimal weight) {
        return weightMax == null || weight.compareTo(weightMax) <= 0;
    }

    public boolean isSuitable(BigDecimal weight, DeliveryZone deliveryZone) {
        return this.isActive()
                && this.isWeightSuitable(weight)
                && this.deliveryZone == deliveryZone;
    }
}