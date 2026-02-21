/**
 * Выходной адаптер для работы с накладными через JPA.
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>WaybillEntity</b> - JPA сущность для БД</li>
 *   <li><b>WaybillJpaRepository</b> - Spring Data JPA репозиторий</li>
 *   <li><b>WaybillPersistenceMapper</b> - Преобразование Domain ↔ Entity</li>
 *   <li><b>WaybillPersistenceAdapter</b> - Реализация интерфейса WaybillRepository</li>
 * </ul>
 *
 * <p>Паттерн Adapter обеспечивает, что использование JPA скрыто от бизнес-логики.
 */
package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;