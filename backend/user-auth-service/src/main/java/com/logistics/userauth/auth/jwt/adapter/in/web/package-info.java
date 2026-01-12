/**
 * Web-адаптер (adapter.in) для JWT аутентификации и управления токенами.
 *
 * <h2>Назначение</h2>
 * Пакет содержит HTTP/API слой, который принимает запросы, валидирует входные данные
 * и делегирует выполнение бизнес-сценариев в application-слой через inbound ports.
 *
 * <h2>Структура</h2>
 * <ul>
 *   <li><b>web</b> — контроллеры/обработчики HTTP запросов и сборка ответов.</li>
 *   <li><b>dto</b> — request/response модели, используемые в REST API.</li>
 * </ul>
 *
 * <h2>DTO</h2>
 * <ul>
 *   <li>{@link com.logistics.userauth.auth.jwt.adapter.in.web.dto.RefreshTokenRequest}</li>
 *   <li>{@link com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse}</li>
 * </ul>
 *
 * @see com.logistics.userauth.auth.jwt.adapter.in.web.dto
 */
package com.logistics.userauth.auth.jwt.adapter.in.web;
