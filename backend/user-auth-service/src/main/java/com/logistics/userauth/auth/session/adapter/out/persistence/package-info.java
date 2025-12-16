/**
 * Выходной адаптер для работы с сессиями через JPA.
 *
 * Содержит:
 * <ul>
 *   <li><b>UserSessionEntity</b> - JPA сущность для БД</li>
 *   <li><b>UserSessionJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>UserSessionPersistenceMapper</b> - Преобразование Domain ↔ Entity</li>
 *   <li><b>UserSessionPersistenceAdapter</b> - Реализация интерфейса UserSessionRepository</li>
 * </ul>
 *
 * Паттерн Adapter обеспечивает, что использование JPA скрыто от бизнес-логики.
 */
package com.logistics.userauth.auth.session.adapter.out.persistence;