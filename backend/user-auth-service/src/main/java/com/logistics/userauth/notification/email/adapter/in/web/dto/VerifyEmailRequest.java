package com.logistics.userauth.notification.email.adapter.in.web.dto;

import com.logistics.userauth.notification.email.adapter.in.validation.EmailCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO для подтверждения email через код верификации.
 *
 * <h2>Назначение</h2>
 * Используется при верификации email после регистрации или смене email.
 *
 * <h2>Пример</h2>
 * <pre>
 * {
 *   "email": "user@example.com",
 *   "code": "123456"
 * }
 * </pre>
 */
@Builder
public record VerifyEmailRequest(
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        String email,

        @NotBlank(message = "Код подтверждения обязателен")
        @EmailCode
        String code
) {
}