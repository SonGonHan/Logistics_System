package com.logistics.userauth.audit.adapter.out.persistence;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import com.logistics.userauth.audit.application.port.out.AuditLogRepository;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.auth.session.adapter.out.persistence.UserSessionJpaRepository;
import com.logistics.userauth.auth.session.application.port.out.UserSessionRepository;
import com.logistics.userauth.user.adapter.out.persistence.UserPersistenceMapper;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Адаптер, реализующий интерфейс AuditLogRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс AuditLogRepository определяет контракт
 * - AuditLogPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * @implements AuditLogRepository
 * @see AuditLogRepository для контракта
 * @see AuditLogJpaRepository для JPA работы
 */
@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepo;
    private final AuditLogPersistenceMapper auditLogMapper;
    private final UserPersistenceMapper userMapper;
    private final AuditActionTypeMapper auditActionTypeMapper;

    @Override
    public void save(AuditLog auditLog) {
        AuditLogEntity auditLogEntity = auditLogMapper.toEntity(auditLog);
        jpaRepo.save(auditLogEntity);
    }

    @Override
    public void delete(AuditLog auditLog) {
        AuditLogEntity auditLogEntity = auditLogMapper.toEntity(auditLog);
        jpaRepo.delete(auditLogEntity);
    }

    @Override
    public List<AuditLog> findByUser(User user) {
        return jpaRepo.findByUser(userMapper.toEntity(user)).stream().map(auditLogMapper::toDomain).toList();
    }

    @Override
    public Optional<AuditLog> findByActionType(AuditActionType actionType) {
        return jpaRepo.findByActionType(auditActionTypeMapper.toEntity(actionType)).map(auditLogMapper::toDomain);
    }

    @Override
    public Optional<AuditLog> findByActorIdentifier(String actorIdentifier) {
        return jpaRepo.findByActorIdentifier(actorIdentifier).map(auditLogMapper::toDomain);
    }
}
