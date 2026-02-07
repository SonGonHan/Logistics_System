package com.logistics.shared.pricing_rule.persistence;

import com.logistics.shared.pricing_rule.domain.DeliveryZone;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA-сущность для правила ценообразования.
 *
 * <h2>Схема хранения</h2>
 * - Схема: shared_data
 * - Таблица: pricing_rules
 * - Последовательность: pricing_rules_pricing_rule_id_seq
 *
 * <h2>Связи</h2>
 * Хранит правила расчета стоимости доставки для разных зон и весовых категорий.
 * Enum deliveryZone сохраняется как строка (STRING).
 *
 * @see com.logistics.shared.pricing_rule.domain.PricingRule доменная модель
 * @see PricingRuleMapper для преобразования Entity ↔ Domain
 */
@Entity
@Table(
        name = "pricing_rules",
        schema = "shared_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "pricing_rule_id")
        }
)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PricingRuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pricing_seq")
    @SequenceGenerator(
            name = "pricing_seq",
            sequenceName = "pricing_rules_pricing_rule_id_seq",
            schema = "shared_data",
            allocationSize = 1
    )
    @Column(name = "pricing_rule_id")
    private Long id;

    @Column(name = "rule_name")
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_zone", nullable = false)
    private DeliveryZone deliveryZone;

    @Column(name = "weight_min")
    private BigDecimal weightMin;

    @Column(name = "weight_max")
    private BigDecimal weightMax;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "price_per_kg")
    private BigDecimal pricePerKg;

    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
}
