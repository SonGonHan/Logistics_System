/**
 * Swagger/OpenAPI документация для REST endpoints.
 *
 * <p>Содержит интерфейсы с @Operation аннотациями для каждого endpoint:
 * <ul>
 *   <li><b>CreateDraftOperation</b> - документация для создания черновика</li>
 *   <li><b>UpdateDraftOperation</b> - документация для обновления черновика</li>
 *   <li><b>GetDraftOperation</b> - получение черновика по ID</li>
 *   <li><b>GetDraftByBarcodeOperation</b> - получение черновика по штрих-коду</li>
 *   <li><b>DeleteDraftOperation</b> - удаление черновика</li>
 *   <li><b>GetUserDraftListOperation</b> - список черновиков пользователя</li>
 * </ul>
 *
 * <p>Паттерн: контроллеры реализуют эти интерфейсы, чтобы отделить
 * Swagger документацию от бизнес-логики контроллера и улучшить читаемость.
 */
package com.logistics.corebusiness.waybill.adapter.in.web.api;