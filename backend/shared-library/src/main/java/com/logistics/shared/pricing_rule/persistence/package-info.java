/**
 * Слой persistence для правил ценообразования.
 *
 * Содержит JPA-сущности и инфраструктуру работы с БД:
 * - {@link com.logistics.shared.pricing_rule.persistence.PricingRuleEntity} — JPA-сущность
 * - {@link com.logistics.shared.pricing_rule.persistence.PricingRuleJpaRepository} — Spring Data репозиторий
 * - {@link com.logistics.shared.pricing_rule.persistence.PricingRuleMapper} — маппер Entity ↔ Domain
 *
 * Работает с таблицей shared_data.pricing_rules.
 */
package com.logistics.shared.pricing_rule.persistence;