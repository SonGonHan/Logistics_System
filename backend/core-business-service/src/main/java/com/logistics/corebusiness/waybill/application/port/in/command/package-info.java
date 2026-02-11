/**
 * Command объекты для use cases модуля накладных.
 *
 * <p>Содержит immutable records, представляющие команды (намерения) пользователя:
 * <ul>
 *   <li><b>CreateDraftCommand</b> - создать черновик</li>
 *   <li><b>UpdateDraftCommand</b> - обновить черновик</li>
 *   <li><b>GetDraftCommand</b> - получить черновик по ID</li>
 *   <li><b>DeleteDraftCommand</b> - удалить черновик</li>
 *   <li><b>GetUserDraftListCommand</b> - получить список черновиков пользователя</li>
 * </ul>
 *
 * <p>Commands передаются из адаптеров в use cases и содержат только
 * необходимые для выполнения операции данные, уже прошедшие валидацию.
 */
package com.logistics.corebusiness.waybill.application.port.in.command;