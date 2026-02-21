/**
 * Подсистема управления накладными (waybills).
 *
 * <p>Отвечает за полный жизненный цикл накладных:
 * <ul>
 *   <li>Создание черновиков накладных (Draft)</li>
 *   <li>Приемку посылок и создание подтвержденных накладных (Waybill)</li>
 *   <li>Отслеживание статусов и истории изменений (WaybillStatusHistory)</li>
 *   <li>Расчет стоимости доставки на основе тарифных правил</li>
 * </ul>
 *
 * <p>Архитектура: Hexagonal (Clean Architecture)
 * <ul>
 *   <li><b>domain</b> - доменные модели и бизнес-логика</li>
 *   <li><b>application</b> - use cases и порты</li>
 *   <li><b>adapter</b> - адаптеры для входа (REST) и выхода (БД)</li>
 * </ul>
 */
package com.logistics.corebusiness.waybill;