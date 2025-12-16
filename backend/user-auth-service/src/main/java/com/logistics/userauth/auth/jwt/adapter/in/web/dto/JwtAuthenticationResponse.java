package com.logistics.userauth.auth.jwt.adapter.in.web.dto;

import lombok.Builder;

/**
 * Ответ с JWT токенами после успешной аутентификации или регистрации.
 *
 * Содержит:
 * - accessToken: JWT токен для доступа к защищенным ресурсам (TTL: 15-60 минут)
 * - refreshToken: Токен для получения нового accessToken без повторной аутентификации (TTL: 7-30 дней)
 *
 * Пример:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjk3NTAwMzIzfQ.xxxx",
 *   "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
 * }
 *
 * Использование:
 * 1. accessToken используется в header: Authorization: Bearer {accessToken}
 * 2. refreshToken хранится безопасно на клиенте (при истечении accessToken)
 */
@Builder
public record JwtAuthenticationResponse (String accessToken, String refreshToken) {
}