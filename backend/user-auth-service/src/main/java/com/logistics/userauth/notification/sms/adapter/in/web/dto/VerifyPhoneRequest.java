package com.logistics.userauth.notification.sms.adapter.in.web.dto;

import com.logistics.shared.validation.Phone;
import com.logistics.userauth.notification.sms.adapter.in.validation.SmsCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO для подтверждения номера телефона через SMS код.
 *
 * <h2>Назначение</h2>
 * Используется при верификации телефона после регистрации для защиты от фейковых аккаунтов.
 *
 * <h2>Пример</h2>
 * <pre>
 * {
 *   "phone": "79991234567",
 *   "code": "123456"
 * }
 * </pre>
 */
@Builder
public record VerifyPhoneRequest(
        @Phone
        String phone,

        @NotBlank(message = "Код подтверждения обязателен")
        @SmsCode
        String code
) {}
