package com.logistics.userauth.sms.adapter.in.web.dto;

import com.logistics.shared.validation.Phone;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO для запроса отправки SMS кода верификации.
 *
 *
 * <h2>Назначение</h2>
 * Используется при верификации телефона после регистрации для защиты от фейковых аккаунтов.
 *
 * <h2>Пример</h2>
 * <pre>
 * {
 *   "phone": "79991234567"
 * }
 * </pre>
 */
@Builder
public record SendVerificationCodeRequest(
        @NotNull(message = "Номер телефона обязателен")
        @Phone
        String phone
) {}
