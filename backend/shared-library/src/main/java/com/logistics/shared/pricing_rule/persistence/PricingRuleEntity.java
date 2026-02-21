package com.logistics.shared.pricing_rule.persistence;

import com.logistics.shared.pricing_rule.domain.DeliveryZone;
import com.logistics.shared.pricing_rule.domain.Dimensions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA-сущность для правила ценообразования.
 *
 * <h2>Схема хранения</h2>
 * - Схема: shared_data
 * - Таблица: pricing_rules
 * - Последовательность: pricing_rules_pricing_rule_id_seq
 *
 * <h2>Поля габаритов</h2>
 * maxDimensions хранится как @Embedded (3 колонки: max_length_cm, max_width_cm, max_height_cm).
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

    @Column(name = "weight_max")
    private BigDecimal weightMax;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "length", column = @Column(name = "max_length_cm", precision = 8, scale = 2)),
            @AttributeOverride(name = "width",  column = @Column(name = "max_width_cm",  precision = 8, scale = 2)),
            @AttributeOverride(name = "height", column = @Column(name = "max_height_cm", precision = 8, scale = 2))
    })
    private Dimensions maxDimensions;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;
}