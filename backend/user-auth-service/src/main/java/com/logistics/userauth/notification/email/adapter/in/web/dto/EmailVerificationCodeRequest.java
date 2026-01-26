package com.logistics.userauth.notification.email.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO для запроса отправки Email кода верификации.
 *
 * <h2>Назначение</h2>
 * Используется при верификации email после регистрации или смене email.
 *
 * <h2>Пример</h2>
 * <pre>
 * {
 *   "email": "user@example.com"
 * }
 * </pre>
 */
@Builder
public record EmailVerificationCodeRequest(
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email
) {
}