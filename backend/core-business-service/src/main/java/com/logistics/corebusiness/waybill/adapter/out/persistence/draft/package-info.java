/**
 * Выходной адаптер для работы с черновиками накладных через JPA.
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>WaybillDraftEntity</b> - JPA сущность для БД</li>
 *   <li><b>WaybillDraftJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>WaybillDraftPersistenceMapper</b> - Преобразование Domain ↔ Entity</li>
 *   <li><b>WaybillDraftPersistenceAdapter</b> - Реализация интерфейса WaybillDraftRepository</li>
 * </ul>
 *
 * <p>Паттерн Adapter обеспечивает, что использование JPA скрыто от бизнес-логики.
 */
package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;