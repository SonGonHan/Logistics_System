/**
 * Inbound ports (use cases) модуля накладных.
 *
 * <p>Содержит интерфейсы, описывающие сценарии использования (application layer),
 * которые вызываются из входных адаптеров (например, REST контроллеров).
 *
 * <p>Примеры будущих use cases:
 * <ul>
 *   <li>CreateDraftUseCase - создание черновика накладной</li>
 *   <li>AcceptWaybillUseCase - приемка посылки и создание накладной</li>
 *   <li>UpdateWaybillStatusUseCase - обновление статуса</li>
 *   <li>TrackWaybillUseCase - отслеживание накладной</li>
 *   <li>CalculatePriceUseCase - расчет стоимости</li>
 * </ul>
 *
 * <p>Реализации этих интерфейсов будут располагаться в пакете
 * {@code com.logistics.corebusiness.waybill.application.usecase}.
 */
package com.logistics.corebusiness.waybill.application.port.in;