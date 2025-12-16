/**
 * Выходной адаптер для работы с пользователями через JPA.
 *
 * Содержит:
 * <ul>
 *   <li><b>UserEntity</b> - JPA сущность для БД</li>
 *   <li><b>UserJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>UserPersistenceMapper</b> - Преобразование Domain ↔ Entity</li>
 *   <li><b>UserPersistenceAdapter</b> - Реализация интерфейса UserRepository</li>
 * </ul>
 *
 * Паттерн Adapter обеспечивает, что использование JPA скрыто от бизнес-логики.
 */
package com.logistics.userauth.user.adapter.out.persistence;