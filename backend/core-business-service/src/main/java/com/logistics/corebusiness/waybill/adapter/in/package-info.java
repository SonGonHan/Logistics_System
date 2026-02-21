/**
 * Входные адаптеры для подсистемы накладных.
 *
 * <p>Содержит компоненты для взаимодействия с внешним миром:
 * <ul>
 *   <li><b>web</b> - REST контроллеры и DTOs</li>
 *   <li><b>DraftControllerMapper</b> - маппер для преобразования между DTOs и Commands/Domain</li>
 * </ul>
 *
 * <p>Входные адаптеры принимают запросы извне, валидируют их,
 * преобразуют в команды и вызывают use cases из application layer.
 */
package com.logistics.corebusiness.waybill.adapter.in;