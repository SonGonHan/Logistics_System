package com.logistics.userauth.auth.jwt.adapter.in.web.dto;

/**
 * Запрос для обновления access token или выхода из системы.
 *
 * Используется для:
 * 1. POST /auth/refresh - получить новый access token
 * 2. POST /auth/logout - отозвать (invalidate) текущую сессию
 *
 * Пример:
 * {
 *   "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
 * }
 */
public record RefreshTokenRequest(String refreshToken) {
}
