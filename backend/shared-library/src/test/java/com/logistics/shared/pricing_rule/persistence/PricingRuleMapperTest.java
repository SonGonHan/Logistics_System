package com.logistics.shared.pricing_rule.persistence;

import com.logistics.shared.pricing_rule.domain.DeliveryZone;
import com.logistics.shared.pricing_rule.domain.Dimensions;
import com.logistics.shared.pricing_rule.domain.PricingRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PricingRuleMapper: юнит-тесты")
class PricingRuleMapperTest {

    private PricingRuleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PricingRuleMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        LocalDate now = LocalDate.now();
        PricingRuleEntity entity = PricingRuleEntity.builder()
                .id(1L)
                .ruleName("Городская доставка до 10 кг")
                .deliveryZone(DeliveryZone.CITY)
                .weightMax(new BigDecimal("10.0"))
                .maxDimensions(Dimensions.of(new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("30")))
                .basePrice(new BigDecimal("200.00"))
                .effectiveFrom(now.minusDays(1))
                .effectiveTo(now.plusDays(30))
                .build();

        // When
        PricingRule domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getRuleName()).isEqualTo("Городская доставка до 10 кг");
        assertThat(domain.getDeliveryZone()).isEqualTo(DeliveryZone.CITY);
        assertThat(domain.getWeightMax()).isEqualByComparingTo(new BigDecimal("10.0"));
        assertThat(domain.getBasePrice()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(domain.getMaxDimensions()).isEqualTo(Dimensions.of(new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("30")));
        assertThat(domain.getEffectiveFrom()).isEqualTo(entity.getEffectiveFrom());
        assertThat(domain.getEffectiveTo()).isEqualTo(entity.getEffectiveTo());
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        LocalDate now = LocalDate.now();
        PricingRule domain = PricingRule.builder()
                .id(2L)
                .ruleName("Региональная доставка")
                .deliveryZone(DeliveryZone.REGIONAL)
                .weightMax(new BigDecimal("50.0"))
                .maxDimensions(Dimensions.of(new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("30")))
                .basePrice(new BigDecimal("1000.00"))
                .effectiveFrom(now.minusDays(10))
                .effectiveTo(now.plusDays(60))
                .build();

        // When
        PricingRuleEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getRuleName()).isEqualTo("Региональная доставка");
        assertThat(entity.getDeliveryZone()).isEqualTo(DeliveryZone.REGIONAL);
        assertThat(entity.getWeightMax()).isEqualByComparingTo(new BigDecimal("50.0"));
        assertThat(entity.getBasePrice()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(entity.getMaxDimensions()).isEqualTo(Dimensions.of(new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("30")));
        assertThat(entity.getEffectiveFrom()).isEqualTo(domain.getEffectiveFrom());
        assertThat(entity.getEffectiveTo()).isEqualTo(domain.getEffectiveTo());
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity с null полями")
    void shouldMapEntityWithNullFields() {
        // Given
        PricingRuleEntity entity = PricingRuleEntity.builder()
                .id(3L)
                .ruleName("Базовое правило")
                .deliveryZone(DeliveryZone.INTERCITY)
                .weightMax(null)
                .maxDimensions(null)
                .basePrice(new BigDecimal("100.00"))
                .effectiveFrom(null)
                .effectiveTo(null)
                .build();

        // When
        PricingRule domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getWeightMax()).isNull();
        assertThat(domain.getMaxDimensions()).isNull();
        assertThat(domain.getEffectiveFrom()).isNull();
        assertThat(domain.getEffectiveTo()).isNull();
    }
}
