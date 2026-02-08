package com.logistics.shared.pricing_rule.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA репозиторий для правил ценообразования.
 *
 * <h2>Назначение</h2>
 * Обеспечивает базовый CRUD-доступ к таблице shared_data.pricing_rules.
 * Используется внутри {@link com.logistics.shared.pricing_rule.PricingRuleService}.
 *
 * @see PricingRuleEntity JPA-сущность
 */
@Repository
public interface PricingRuleJpaRepository extends JpaRepository<PricingRuleEntity, Long> {
}
