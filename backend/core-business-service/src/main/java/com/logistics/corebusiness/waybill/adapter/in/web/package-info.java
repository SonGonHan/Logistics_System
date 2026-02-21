/**
 * REST контроллеры для управления накладными.
 *
 * <p>Содержит:
 * <ul>
 *   <li><b>DraftController</b> - REST API для работы с черновиками накладных</li>
 *   <li><b>dto</b> - объекты передачи данных (DTOs) для request/response</li>
 *   <li><b>api</b> - Swagger @Operation аннотации для документирования API</li>
 * </ul>
 *
 * <p>Контроллеры отвечают за:
 * - Валидацию входных данных (Jakarta Validation)
 * - Маппинг DTOs в Commands
 * - Вызов соответствующих use cases
 * - Преобразование результатов в response DTOs
 */
package com.logistics.corebusiness.waybill.adapter.in.web;