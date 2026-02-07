package com.logistics.shared.pricing_rule.persistence;

import com.logistics.shared.pricing_rule.PricingRuleService;
import com.logistics.shared.pricing_rule.domain.PricingRule;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования правил ценообразования между слоями.
 *
 * <h2>Ответственность</h2>
 * Выполняет двунаправленное преобразование:
 * - PricingRuleEntity (слой persistence) → PricingRule (domain)
 * - PricingRule (domain) → PricingRuleEntity (persistence)
 *
 * <h2>Использование</h2>
 * Используется в {@link PricingRuleService} для изоляции доменной логики от деталей хранения.
 */
@Component
public class PricingRuleMapper {

    public PricingRule toDomain(PricingRuleEntity entity) {
        return PricingRule.builder()
                .id(entity.getId())
                .ruleName(entity.getRuleName())
                .deliveryZone(entity.getDeliveryZone())
                .weightMin(entity.getWeightMin())
                .weightMax(entity.getWeightMax())
                .basePrice(entity.getBasePrice())
                .pricePerKg(entity.getPricePerKg())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .build();
    }

    public  PricingRuleEntity toEntity(PricingRule domain) {
        return PricingRuleEntity.builder()
                .id(domain.getId())
                .ruleName(domain.getRuleName())
                .deliveryZone(domain.getDeliveryZone())
                .weightMin(domain.getWeightMin())
                .weightMax(domain.getWeightMax())
                .basePrice(domain.getBasePrice())
                .pricePerKg(domain.getPricePerKg())
                .effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo())
                .build();
    }
}
