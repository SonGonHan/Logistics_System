package com.logistics.userauth.audit.adapter.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.user.adapter.out.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA репозиторий для работы с сессия.
 *
 * <h2>Методы</h2>
 * Наследует от JpaRepository:
 * - save, saveAll, delete, deleteAll, findById, findAll и т.д.
 *
 * Плюс кастомные методы для поиска:
 * - findByUser(user)
 * - findByActionType(actionType)
 * - findByActorIdentifier(actorIdentifier)
 *
 * @see AuditLogEntity для сущности
 * @see AuditLogPersistenceAdapter для использования в бизнес-логике
 */
@Repository
interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findByUser(UserEntity user);

    Optional<AuditLogEntity> findByActionType(AuditActionTypeEntity actionType);

    Optional<AuditLogEntity> findByActorIdentifier(String actorIdentifier);

}
