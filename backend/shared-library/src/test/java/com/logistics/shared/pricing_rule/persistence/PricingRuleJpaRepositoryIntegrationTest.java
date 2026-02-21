package com.logistics.shared.pricing_rule.persistence;

import com.logistics.shared.IntegrationTest;
import com.logistics.shared.pricing_rule.domain.DeliveryZone;
import com.logistics.shared.pricing_rule.domain.Dimensions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("PricingRuleJpaRepository: интеграционные тесты")
class PricingRuleJpaRepositoryIntegrationTest {

    @Autowired
    private PricingRuleJpaRepository repository;

    @Test
    @DisplayName("Должен сохранить и найти PricingRule по ID")
    void shouldSaveAndFindById() {
        // Given
        PricingRuleEntity entityToSave = PricingRuleEntity.builder()
                .ruleName("Тестовое правило")
                .deliveryZone(DeliveryZone.CITY)
                .weightMax(new BigDecimal("10.0"))
                .maxDimensions(Dimensions.of(new BigDecimal("60"), new BigDecimal("40"), new BigDecimal("30")))
                .basePrice(new BigDecimal("200.00"))
                .effectiveFrom(LocalDate.now().minusDays(1))
                .effectiveTo(LocalDate.now().plusDays(30))
                .build();

        // Сохраняем и получаем обратно экземпляр с ID
        PricingRuleEntity savedEntity = repository.save(entityToSave);

        // When
        Optional<PricingRuleEntity> found = repository.findById(savedEntity.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRuleName()).isEqualTo("Тестовое правило");
        assertThat(found.get().getDeliveryZone()).isEqualTo(DeliveryZone.CITY);
        assertThat(found.get().getId()).isEqualTo(savedEntity.getId());
    }

    @Test
    @DisplayName("Должен сохранить правило с null полями")
    void shouldSaveRuleWithNullFields() {
        // Given
        PricingRuleEntity entityToSave = PricingRuleEntity.builder()
                .ruleName("Правило без ограничений")
                .deliveryZone(DeliveryZone.INTERCITY)
                .weightMax(null)
                .maxDimensions(null)
                .basePrice(new BigDecimal("300.00"))
                .effectiveFrom(null)
                .effectiveTo(null)
                .build();

        // When
        PricingRuleEntity savedEntity = repository.save(entityToSave);
        Optional<PricingRuleEntity> found = repository.findById(savedEntity.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getMaxDimensions()).isNull();
        assertThat(found.get().getWeightMax()).isNull();
        assertThat(found.get().getEffectiveFrom()).isNull();
        assertThat(found.get().getEffectiveTo()).isNull();
    }

    @Test
    @DisplayName("Должен сохранить правила с разными зонами доставки")
    void shouldSaveRulesWithDifferentZones() {
        // Given
        PricingRuleEntity cityRule = PricingRuleEntity.builder()
                .ruleName("Городская доставка")
                .deliveryZone(DeliveryZone.CITY)
                .basePrice(new BigDecimal("100.00"))
                .build();

        PricingRuleEntity intercityRule = PricingRuleEntity.builder()
                .ruleName("Межгородская доставка")
                .deliveryZone(DeliveryZone.INTERCITY)
                .basePrice(new BigDecimal("300.00"))
                .build();

        PricingRuleEntity suburbanRule = PricingRuleEntity.builder()
                .ruleName("Пригородная доставка")
                .deliveryZone(DeliveryZone.SUBURBAN)
                .basePrice(new BigDecimal("1000.00"))
                .build();

        // When
        PricingRuleEntity savedCity = repository.save(cityRule);
        PricingRuleEntity savedIntercity = repository.save(intercityRule);
        PricingRuleEntity savedSuburban = repository.save(suburbanRule);

        // Then
        assertThat(savedCity.getDeliveryZone()).isEqualTo(DeliveryZone.CITY);
        assertThat(savedIntercity.getDeliveryZone()).isEqualTo(DeliveryZone.INTERCITY);
        assertThat(savedSuburban.getDeliveryZone()).isEqualTo(DeliveryZone.SUBURBAN);
    }
}
