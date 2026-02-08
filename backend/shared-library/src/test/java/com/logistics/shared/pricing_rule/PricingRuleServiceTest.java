package com.logistics.shared.pricing_rule;

import com.logistics.shared.pricing_rule.domain.DeliveryZone;
import com.logistics.shared.pricing_rule.domain.PricingRule;
import com.logistics.shared.pricing_rule.persistence.PricingRuleEntity;
import com.logistics.shared.pricing_rule.persistence.PricingRuleJpaRepository;
import com.logistics.shared.pricing_rule.persistence.PricingRuleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PricingRuleService: юнит-тесты")
class PricingRuleServiceTest {

    @Mock
    private PricingRuleJpaRepository repository;
    @Mock
    private PricingRuleMapper mapper;
    @InjectMocks
    private PricingRuleService service;

    private PricingRuleEntity testEntity;
    private PricingRule testDomain;

    @BeforeEach
    void setUp() {
        testEntity = PricingRuleEntity.builder()
                .id(1L)
                .ruleName("Городская доставка")
                .deliveryZone(DeliveryZone.CITY)
                .weightMin(new BigDecimal("5.0"))
                .weightMax(new BigDecimal("10.0"))
                .basePrice(new BigDecimal("200.00"))
                .pricePerKg(new BigDecimal("50.00"))
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .build();

        testDomain = PricingRule.builder()
                .id(1L)
                .ruleName("Городская доставка")
                .deliveryZone(DeliveryZone.CITY)
                .weightMin(new BigDecimal("5.0"))
                .weightMax(new BigDecimal("10.0"))
                .basePrice(new BigDecimal("200.00"))
                .pricePerKg(new BigDecimal("50.00"))
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    @DisplayName("Должен рассчитать стоимость доставки по правилу")
    void shouldCalculatePrice() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(any(PricingRuleEntity.class))).thenReturn(testDomain);

        // When
        BigDecimal price = service.calculatePrice(1L, new BigDecimal("7.5"));

        // Then
        // basePrice (200) + pricePerKg (50) * weight (7.5) = 200 + 375 = 575.00
        assertThat(price).isEqualByComparingTo(new BigDecimal("575.00"));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если правило не найдено")
    void shouldThrowExceptionWhenRuleNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.calculatePrice(999L, new BigDecimal("7.5")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Правило ценообразования не найдено");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если правило неактивно")
    void shouldThrowExceptionWhenRuleNotActive() {
        // Given
        PricingRule inactiveRule = PricingRule.builder()
                .id(1L)
                .ruleName("Неактивное правило")
                .deliveryZone(DeliveryZone.CITY)
                .weightMin(new BigDecimal("5.0"))
                .weightMax(new BigDecimal("10.0"))
                .basePrice(new BigDecimal("200.00"))
                .pricePerKg(new BigDecimal("50.00"))
                .effectiveFrom(LocalDateTime.now().plusDays(1))
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(any(PricingRuleEntity.class))).thenReturn(inactiveRule);

        // When & Then
        assertThatThrownBy(() -> service.calculatePrice(1L, new BigDecimal("7.5")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Правило ценообразования неактивно");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если вес не соответствует диапазону")
    void shouldThrowExceptionWhenWeightNotSuitable() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(any(PricingRuleEntity.class))).thenReturn(testDomain);

        // When & Then
        assertThatThrownBy(() -> service.calculatePrice(1L, new BigDecimal("15.0")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Вес не соответствует диапазону правила");
    }

    @Test
    @DisplayName("Должен найти подходящее правило по весу и зоне")
    void shouldFindSuitableRule() {
        // Given
        List<PricingRuleEntity> entities = Arrays.asList(testEntity);
        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDomain(any(PricingRuleEntity.class))).thenReturn(testDomain);

        // When
        Optional<PricingRule> result = service.findSuitableRule(new BigDecimal("7.5"), DeliveryZone.CITY);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, если подходящее правило не найдено")
    void shouldReturnEmptyWhenNoSuitableRule() {
        // Given
        List<PricingRuleEntity> entities = Arrays.asList(testEntity);
        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDomain(any(PricingRuleEntity.class))).thenReturn(testDomain);

        // When
        Optional<PricingRule> result = service.findSuitableRule(new BigDecimal("7.5"), DeliveryZone.REGIONAL);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть список активных правил")
    void shouldGetActiveRules() {
        // Given
        PricingRule activeRule1 = PricingRule.builder()
                .id(1L)
                .deliveryZone(DeliveryZone.CITY)
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .build();

        PricingRule activeRule2 = PricingRule.builder()
                .id(2L)
                .deliveryZone(DeliveryZone.INTERCITY)
                .effectiveFrom(LocalDateTime.now().minusDays(5))
                .effectiveTo(LocalDateTime.now().plusDays(60))
                .build();

        PricingRule inactiveRule = PricingRule.builder()
                .id(3L)
                .deliveryZone(DeliveryZone.REGIONAL)
                .effectiveFrom(LocalDateTime.now().plusDays(1))
                .effectiveTo(LocalDateTime.now().plusDays(90))
                .build();

        List<PricingRuleEntity> entities = Arrays.asList(
                testEntity, testEntity, testEntity
        );

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDomain(any(PricingRuleEntity.class)))
                .thenReturn(activeRule1)
                .thenReturn(activeRule2)
                .thenReturn(inactiveRule);

        // When
        List<PricingRule> activeRules = service.getActiveRules();

        // Then
        assertThat(activeRules).hasSize(2);
        assertThat(activeRules).extracting(PricingRule::getId).containsExactly(1L, 2L);
    }
}
