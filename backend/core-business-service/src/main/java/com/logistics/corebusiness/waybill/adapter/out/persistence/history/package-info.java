/**
 * Выходной адаптер для работы с историей статусов накладных через JPA.
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>WaybillStatusHistoryEntity</b> - JPA сущность для БД</li>
 *   <li><b>WaybillStatusHistoryJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>WaybillStatusHistoryPersistenceMapper</b> - Преобразование Domain ↔ Entity</li>
 *   <li><b>WaybillStatusHistoryPersistenceAdapter</b> - Реализация интерфейса WaybillStatusHistoryRepository</li>
 * </ul>
 *
 * <p>Паттерн Adapter обеспечивает, что использование JPA скрыто от бизнес-логики.
 * <p>История статусов - append-only таблица для полного аудита движения посылки.
 */
package com.logistics.corebusiness.waybill.adapter.out.persistence.history;