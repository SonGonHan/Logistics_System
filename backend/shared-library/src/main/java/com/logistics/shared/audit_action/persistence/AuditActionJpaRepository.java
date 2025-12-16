package com.logistics.shared.audit_action.persistence;

import com.logistics.shared.audit_action.AuditActionTypeService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA репозиторий для работы с типами аудит-действий.
 *
 * <h2>Примеры использования</h2>
 * <pre>
 * Optional<AuditActionTypeEntity> entity = repo.findById(1);
 * Optional<AuditActionTypeEntity> entity = repo.findByActionName(\"USER_LOGIN\");
 * </pre>
 *
 * @see AuditActionTypeEntity для сущности
 * @see AuditActionTypeService для бизнес-логики
 */
@Repository
public interface AuditActionJpaRepository extends JpaRepository<AuditActionTypeEntity, Integer> {

    Optional<AuditActionTypeEntity> findByCategory(String category);

    Optional<AuditActionTypeEntity> findByActionName(String actionName);
}
