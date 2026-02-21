/**
 * Выходной адаптер для работы с черновиками накладных через JPA.
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>DraftEntity</b> - JPA сущность для БД</li>
 *   <li><b>DraftJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>DraftPersistenceMapper</b> - Преобразование Domain ↔ Entity</li>
 *   <li><b>DraftPersistenceAdapter</b> - Реализация интерфейса DraftRepository</li>
 * </ul>
 *
 * <p>Паттерн Adapter обеспечивает, что использование JPA скрыто от бизнес-логики.
 */
package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;