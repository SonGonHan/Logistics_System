package com.logistics.shared.pricing_rule;

import com.logistics.shared.pricing_rule.domain.DeliveryZone;
import com.logistics.shared.pricing_rule.domain.PricingRule;
import com.logistics.shared.pricing_rule.persistence.PricingRuleJpaRepository;
import com.logistics.shared.pricing_rule.persistence.PricingRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Бизнес-сервис для работы с правилами ценообразования.
 *
 * <h2>Ответственность</h2>
 * Предоставляет функциональность для:
 * - Расчета стоимости доставки по конкретному правилу
 * - Поиска подходящего правила по весу и зоне доставки
 * - Получения списка активных правил
 *
 * <h2>Формула расчета</h2>
 * Стоимость = basePrice + (pricePerKg × вес), округление до 2 знаков (HALF_UP)
 *
 * <h2>Примеры использования</h2>
 * <pre>
 * // Расчет стоимости по известному правилу
 * BigDecimal price = service.calculatePrice(ruleId, new BigDecimal("15.5"));
 *
 * // Автоматический поиск правила
 * Optional&lt;PricingRule&gt; rule = service.findSuitableRule(
 *     new BigDecimal("15.5"),
 *     DeliveryZone.CITY
 * );
 * </pre>
 *
 * @see PricingRuleJpaRepository для работы с БД
 * @see PricingRuleMapper для преобразования Entity ↔ Domain
 * @see PricingRule доменная модель
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PricingRuleService {
    private final PricingRuleJpaRepository repository;

    private final PricingRuleMapper mapper;

    /**
     * Рассчитывает стоимость доставки по конкретному правилу.
     *
     * @param pricingRuleId ID правила ценообразования
     * @param weight вес груза в килограммах
     * @return стоимость доставки с округлением до 2 знаков
     * @throws IllegalArgumentException если правило не найдено, неактивно или вес не подходит
     */
    public BigDecimal calculatePrice(Long pricingRuleId, BigDecimal weight) {
        var entity = repository.findById(pricingRuleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Правило ценообразования не найдено: " + pricingRuleId));

        var rule = mapper.toDomain(entity);

        if (!rule.isActive()) {
            log.warn("Правило {} неактивно на дату {}", pricingRuleId, LocalDate.now());
            throw new IllegalArgumentException("Правило ценообразования неактивно");
        }

        if (!rule.isWeightSuitable(weight)) {
            log.warn("Вес {} не попадает в диапазон правила {} [{}, {}]",
                    weight, pricingRuleId, rule.getWeightMin(), rule.getWeightMax());
            throw new IllegalArgumentException("Вес не соответствует диапазону правила");
        }

        var price = rule.getBasePrice();
        price = price.add(rule.getPricePerKg().multiply(weight));

        return price.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Ищет первое подходящее правило по весу и зоне доставки.
     *
     * @param weight вес груза
     * @param deliveryZone зона доставки
     * @return первое найденное активное правило или Optional.empty()
     */
    public Optional<PricingRule> findSuitableRule(BigDecimal weight, DeliveryZone deliveryZone) {
        LocalDate today = LocalDate.now();

        return repository.findAll().stream()
                .map(mapper::toDomain)
                .filter(r -> r.isSuitable(weight, deliveryZone))
                .findFirst();
    }

    /**
     * Возвращает список всех активных правил ценообразования.
     *
     * @return список правил, действующих в текущий момент
     */
    public List<PricingRule> getActiveRules() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .filter(r -> r.isActive())
                .toList();
    }

}
