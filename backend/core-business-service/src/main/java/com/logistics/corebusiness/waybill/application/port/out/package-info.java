/**
 * Outbound ports (repository interfaces) модуля накладных.
 *
 * <p>Содержит интерфейсы для работы с внешними системами (БД, внешние API),
 * которые вызываются из use cases.
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>WaybillRepository</b> - работа с подтвержденными накладными</li>
 *   <li><b>WaybillDraftRepository</b> - работа с черновиками</li>
 *   <li><b>WaybillStatusHistoryRepository</b> - работа с историей статусов</li>
 * </ul>
 *
 * <p>Реализации этих интерфейсов располагаются в пакете
 * {@code com.logistics.corebusiness.waybill.adapter.out.persistence}.
 */
package com.logistics.corebusiness.waybill.application.port.out;