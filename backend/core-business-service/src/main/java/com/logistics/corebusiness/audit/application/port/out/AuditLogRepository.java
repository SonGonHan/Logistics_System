package com.logistics.corebusiness.audit.application.port.out;

import com.logistics.corebusiness.audit.domain.AuditLog;

/**
 * Порт для сохранения логов аудита.
 *
 * <h2>Назначение</h2>
 * Минимальный контракт - только save(). Чтение логов не требуется в core-business-service.
 *
 * @see com.logistics.corebusiness.audit.adapter.out.persistence.AuditLogPersistenceAdapter для реализации
 */
public interface AuditLogRepository {
    void save(AuditLog auditLog);
}
