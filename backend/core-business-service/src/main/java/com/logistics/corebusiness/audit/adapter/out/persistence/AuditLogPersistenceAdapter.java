package com.logistics.corebusiness.audit.adapter.out.persistence;

import com.logistics.corebusiness.audit.application.port.out.AuditLogRepository;
import com.logistics.corebusiness.audit.domain.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Адаптер, реализующий интерфейс AuditLogRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Adapter паттерн из Hexagonal Architecture:
 * - Интерфейс AuditLogRepository определяет контракт (application layer)
 * - AuditLogPersistenceAdapter реализует контракт с помощью JPA (adapter layer)
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * @implements AuditLogRepository
 * @see AuditLogRepository для контракта
 * @see AuditLogJpaRepository для JPA работы
 */
@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepo;
    private final AuditLogPersistenceMapper mapper;

    @Override
    public void save(AuditLog auditLog) {
        var entity = mapper.toEntity(auditLog);
        jpaRepo.save(entity);
    }
}
