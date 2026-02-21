/**
 * Data Transfer Objects (DTOs) для REST API накладных.
 *
 * <p>Request DTOs (входящие данные):
 * <ul>
 *   <li><b>CreateDraftRequest</b> - создание черновика</li>
 *   <li><b>UpdateDraftRequest</b> - обновление черновика</li>
 *   <li><b>DimensionsDto</b> - габариты посылки</li>
 * </ul>
 *
 * <p>Response DTOs (исходящие данные):
 * <ul>
 *   <li><b>DraftResponse</b> - краткая информация о черновике (для списков)</li>
 *   <li><b>DetailedDraftResponse</b> - полная информация о черновике</li>
 * </ul>
 *
 * <p>DTOs изолируют API от внутренних доменных моделей и обеспечивают
 * независимость изменений внутренней структуры от внешнего контракта.
 */
package com.logistics.corebusiness.waybill.adapter.in.web.dto;