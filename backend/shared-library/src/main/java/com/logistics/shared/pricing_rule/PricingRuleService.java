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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Бизнес-сервис для работы с правилами ценообразования.
 *
 * <h2>Ответственность</h2>
 * Предоставляет функциональность для:
 * - Расчета стоимости доставки по конкретному тарифу (фиксированная ставка)
 * - Поиска подходящего тарифа по весу и зоне доставки
 * - Получения списка активных тарифов
 *
 * <h2>Формула расчета</h2>
 * Стоимость = basePrice (фиксированная ставка тарифного плана)
 *
 * <h2>Подбор тарифа</h2>
 * Среди активных правил нужной зоны выбирается то, чей weightMax наименьший
 * и при этом >= фактическому весу (правила отсортированы по weightMax, NULL — последний).
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
     * Рассчитывает стоимость доставки по конкретному тарифному правилу.
     * Цена фиксированная — это basePrice правила.
     *
     * @param pricingRuleId ID правила ценообразования
     * @return стоимость доставки с округлением до 2 знаков
     * @throws IllegalArgumentException если правило не найдено или неактивно
     */
    public BigDecimal calculatePrice(Long pricingRuleId) {
        var entity = repository.findById(pricingRuleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Правило ценообразования не найдено: " + pricingRuleId));

        var rule = mapper.toDomain(entity);

        if (!rule.isActive()) {
            log.warn("Правило {} неактивно на дату {}", pricingRuleId, LocalDate.now());
            throw new IllegalArgumentException("Правило ценообразования неактивно");
        }

        return rule.getBasePrice().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Ищет наиболее подходящий тариф по весу и зоне доставки.
     * Выбирается тариф с наименьшим weightMax >= weight (NULL-weightMax идёт последним).
     *
     * @param weight вес груза
     * @param deliveryZone зона доставки
     * @return подходящий активный тариф или Optional.empty()
     */
    public Optional<PricingRule> findSuitableRule(BigDecimal weight, DeliveryZone deliveryZone) {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .filter(r -> r.isSuitable(weight, deliveryZone))
                .sorted(Comparator.comparing(
                        PricingRule::getWeightMax,
                        Comparator.nullsLast(Comparator.naturalOrder())))
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
                .filter(PricingRule::isActive)
                .toList();
    }

}