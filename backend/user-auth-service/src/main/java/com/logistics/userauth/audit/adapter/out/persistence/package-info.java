/**
 * Выходной адаптер для хранения логов аудита в БД через JPA.
 *
 * Содержит:
 * <ul>
 *   <li><b>AuditLogEntity</b> - JPA сущность</li>
 *   <li><b>AuditLogJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>AuditLogPersistenceMapper</b> - Преобразование Domain ↔ Entity</li>
 *   <li><b>AuditLogPersistenceAdapter</b> - Реализация интерфейса AuditLogRepository</li>
 * </ul>
 */
package com.logistics.userauth.audit.adapter.out.persistence;
