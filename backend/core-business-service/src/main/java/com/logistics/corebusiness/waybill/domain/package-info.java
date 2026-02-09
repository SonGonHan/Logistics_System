/**
 * Доменный слой подсистемы накладных.
 *
 * <p>Содержит чистые доменные модели без зависимостей от фреймворков:
 * <ul>
 *   <li><b>Waybill</b> - подтвержденная накладная (после приемки)</li>
 *   <li><b>WaybillDraft</b> - черновик накладной (до приемки)</li>
 *   <li><b>WaybillStatusHistory</b> - запись истории изменения статуса</li>
 *   <li><b>Dimensions</b> - Value Object для габаритов посылки</li>
 *   <li><b>WaybillStatus</b> - enum статусов накладной</li>
 *   <li><b>DraftStatus</b> - enum статусов черновика</li>
 * </ul>
 *
 * <p>Принципы:
 * - Нет JPA аннотаций (чистый POJO/Record)
 * - Нет Spring зависимостей
 * - Содержит только бизнес-логику
 */
package com.logistics.corebusiness.waybill.domain;