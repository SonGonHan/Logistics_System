/**
 * Пакет инфраструктурного уровня для работы с типами аудиторских действий через JPA.
 *
 * Содержит:
 * <ul>
 *   <li><b>AuditActionTypeEntity</b> - JPA сущность для БД</li>
 *   <li><b>AuditActionJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>AuditActionTypeMapper</b> - Преобразование Entity ↔ Domain</li>
 * </ul>
 */
package com.logistics.shared.audit_action.persistence;