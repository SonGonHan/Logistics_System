/**
 * Адаптеры для подсистемы накладных.
 *
 * <p>Содержит входные и выходные адаптеры:
 * <ul>
 *   <li><b>in</b> - входные адаптеры (REST controllers, DTOs)</li>
 *   <li><b>out</b> - выходные адаптеры (persistence, external APIs)</li>
 * </ul>
 *
 * <p>Паттерн Adapter изолирует бизнес-логику от технических деталей
 * (JPA, Spring MVC, JSON serialization и т.д.).
 */
package com.logistics.corebusiness.waybill.adapter;