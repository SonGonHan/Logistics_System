package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.shared.validation.Password;
import com.logistics.shared.validation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Запрос для авторизации пользователя.
 *
 * <h2>Валидация</h2>
 * - phone: Обязателен, формат +7/375/77...
 * - password: Обязателен, сложный пароль
 * - email: Опциональный альтернативный способ входа
 *
 * <h2>Пример запроса</h2>
 * {
 *   \"phone\": \"+79991234567\",
 *   \"password\": \"Password123!\"
 * }
 */
@Builder
public record SignUpRequest(
        @Email
        String email,

        @NotNull
        @Phone
        String phone,

        @Password
        String password,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String middleName
) {}
