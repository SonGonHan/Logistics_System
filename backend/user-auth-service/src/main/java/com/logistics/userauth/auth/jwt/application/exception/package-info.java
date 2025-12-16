/**
 * Специализированные исключения прикладного уровня JWT-аутентификации.
 *
 * Типичные исключения:
 * <ul>
 *   <li><b>InvalidRefreshTokenException</b> - Refresh token не найден, отозван или истёк</li>
 * </ul>
 *
 * Маппируются в HTTP-ответы через GlobalExceptionHandler (обычно 401 Unauthorized).
 */
package com.logistics.userauth.auth.jwt.application.exception;