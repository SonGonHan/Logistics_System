/**
 * Модуль правил ценообразования для расчета стоимости доставки.
 *
 * <h2>Назначение</h2>
 * Предоставляет централизованную логику расчета стоимости доставки грузов
 * на основе настраиваемых правил, учитывающих:
 * - Зону доставки (город, межгород, международная)
 * - Весовую категорию груза
 * - Период действия правила
 *
 * <h2>Основные компоненты</h2>
 * - {@link com.logistics.shared.pricing_rule.PricingRuleService} — бизнес-сервис для расчетов
 * - {@link com.logistics.shared.pricing_rule.domain.PricingRule} — доменная модель правила
 * - {@link com.logistics.shared.pricing_rule.domain.DeliveryZone} — enum зон доставки
 *
 * <h2>Пример использования</h2>
 * <pre>{@code
 * @Service
 * public class WaybillPricingService {
 *     private final PricingRuleService pricingService;
 *
 *     public BigDecimal calculateDeliveryCost(BigDecimal weight, DeliveryZone zone) {
 *         Optional<PricingRule> rule = pricingService.findSuitableRule(weight, zone);
 *         if (rule.isEmpty()) {
 *             throw new IllegalStateException("Нет подходящего правила");
 *         }
 *         return pricingService.calculatePrice(rule.get().getId(), weight);
 *     }
 * }
 * }</pre>
 *
 * <h2>Хранение данных</h2>
 * Правила хранятся в таблице shared_data.pricing_rules (миграция V1).
 * Используется схема shared-library для переиспользования в разных микросервисах.
 */
package com.logistics.shared.pricing_rule;