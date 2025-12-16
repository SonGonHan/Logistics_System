package com.logistics.userauth.auth.session.adapter.out.persistence;

import com.logistics.userauth.audit.adapter.out.persistence.AuditLogEntity;
import com.logistics.userauth.audit.adapter.out.persistence.AuditLogPersistenceAdapter;
import com.logistics.userauth.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
 * - findByRefreshToken(refreshToken)
 *
 * @see UserSessionEntity для сущности
 * @see UserSessionPersistenceAdapter для использования в бизнес-логике
 */
@Repository
public interface UserSessionJpaRepository extends JpaRepository<UserSessionEntity, Long> {
    Optional<UserSessionEntity> findByUser(User user);

    Optional<UserSessionEntity> findByRefreshToken(String refreshToken);

}
