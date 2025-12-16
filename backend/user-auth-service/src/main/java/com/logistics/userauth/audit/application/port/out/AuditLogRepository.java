package com.logistics.userauth.audit.application.port.out;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.adapter.out.persistence.AuditLogPersistenceAdapter;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Порт для работы с логами аудита.
 *
 * @see AuditLogPersistenceAdapter для реализации
 */
public interface AuditLogRepository {
    void save(AuditLog auditLog);

    void delete(AuditLog auditLog);

    List<AuditLog> findByUser(User user);

    Optional<AuditLog> findByActionType(AuditActionType actionType);

    Optional<AuditLog> findByActorIdentifier(String actorIdentifier);
}
