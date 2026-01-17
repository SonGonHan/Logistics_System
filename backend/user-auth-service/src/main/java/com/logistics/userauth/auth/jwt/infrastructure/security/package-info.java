/**
 * Spring Security конфигурация для JWT-аутентификации.
 *
 * Содержит:
 * <ul>
 *   <li>{@link com.logistics.userauth.auth.jwt.infrastructure.security.SecurityConfiguration} — JWT-фильтр (JwtAuthenticationFilter), правила доступа к endpoint'ам</li>
 *   <li>{@link com.logistics.userauth.auth.jwt.infrastructure.security.WebCorsConfiguration} — CORS для фронтенд-приложений (localhost:3000)</li>
 *   <li>Обработчики ошибок безопасности</li>
 * </ul>
 */
package com.logistics.userauth.auth.jwt.infrastructure.security;
