package com.logistics.userauth.audit.app.out;


import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface AuditLogRepository {
    void save(AuditLog auditLog);

    void delete(AuditLog auditLog);

    List<AuditLog> findByUser(User user);

    Optional<AuditLog> findByActionType(AuditActionType actionType);

    Optional<AuditLog> findByActorIdentifier(String actorIdentifier);
}
