package com.logistics.corebusiness.audit.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA репозиторий для audit_logs.
 *
 * <h2>Назначение</h2>
 * Минимальный репозиторий - только save (наследуется от JpaRepository).
 * Чтение логов не требуется в core-business-service.
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {
}
