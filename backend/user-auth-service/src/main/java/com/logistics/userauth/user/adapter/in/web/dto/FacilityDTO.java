package com.logistics.userauth.user.adapter.in.web.dto;

import lombok.Builder;
/**
 * DTO для передачи информации о здании, где работает сотрудник.
 *
 * <h2>Назначение</h2>
 * Содержит публичную информацию про здание кампании.
 *
 * <h2>Примеры</h2>
 * {
 *   \"name\": \"Склад №1\",
 *   \"address\": \"г. Вологда, ул. Ленинградская, д. 25\",
 * }
 */

@Builder
public record FacilityDTO (String name, String address) {
}
