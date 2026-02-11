/**
 * Корневой пакет для JPA persistence адаптеров накладных.
 *
 * <p>Содержит подпакеты для каждого типа сущности:
 * <ul>
 *   <li><b>draft</b> - работа с черновиками накладных (DraftEntity)</li>
 *   <li><b>waybill</b> - работа с подтвержденными накладными (WaybillEntity)</li>
 *   <li><b>history</b> - история изменения статусов (WaybillStatusHistoryEntity)</li>
 * </ul>
 *
 * <p>Каждый подпакет следует единому паттерну:
 * Entity (JPA) → JpaRepository (Spring Data) → Mapper (Domain ↔ Entity) → Adapter (реализация port.out).
 */
package com.logistics.corebusiness.waybill.adapter.out.persistence;