package com.logistics.userauth.notification.email.adapter.in.web.dto;

import lombok.Builder;

/**
 * DTO ответа после успешной верификации email.
 *
 * <h2>Назначение</h2>
 * Возвращается клиенту после успешной проверки кода верификации.
 *
 * <h2>Пример</h2>
 * <pre>
 * {
 *   "email": "user@example.com",
 *   "verified": true,
 *   "message": "Email успешно верифицирован"
 * }
 * </pre>
 */
@Builder
public record VerifyEmailResponse(
        String email,
        boolean verified,
        String message
) {
}