/**
 * Security-фильтры для проверки JWT-токенов в HTTP-запросах.
 *
 * Содержит:
 * <ul>
 *   <li><b>JwtAuthenticationFilter</b> - Spring Security фильтр для Extract & Validate JWT</li>
 * </ul>
 *
 * Процесс:
 * <ol>
 *   <li>Извлекает JWT из заголовка Authorization (Bearer scheme)</li>
 *   <li>Валидирует подпись и время жизни</li>
 *   <li>Заполняет SecurityContext информацией о пользователе</li>
 *   <li>Если валидация не пройдена - возвращает 401 Unauthorized</li>
 * </ol>
 */
package com.logistics.userauth.auth.jwt.adapter.in.security;