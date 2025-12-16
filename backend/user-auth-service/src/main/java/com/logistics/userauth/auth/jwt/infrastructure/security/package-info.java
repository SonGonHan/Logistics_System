/**
 * Spring Security конфигурация для JWT-аутентификации.
 *
 * Регистрирует:
 * <ul>
 *   <li>JWT-фильтр (JwtAuthenticationFilter)</li>
 *   <li>Правила доступа к endpoint'ам</li>
 *   <li>Обработчики ошибок безопасности</li>
 *   <li>CORS и другие безопасностные header'ы</li>
 * </ul>
 */
package com.logistics.userauth.auth.jwt.infrastructure.security;
