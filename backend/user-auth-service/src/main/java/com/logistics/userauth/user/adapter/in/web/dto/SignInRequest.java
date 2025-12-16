package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.shared.validation.Password;
import com.logistics.shared.validation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Запрос для регистрации нового пользователя.
 *
 * <h2>Валидация</h2>
 * - email: Должен быть в формате email
 * - phone: Формат +7XXXXXXXXXX (РФ), +375XXXXXXXXX (БР), +77XXXXXXXXX (КЗ)
 * - password: Минимум 8 символов, цифра, заглавная, строчная, спецсимвол
 * - firstName, lastName: Не пусты
 *
 * <h2>Пример запроса</h2>
 * {
 *   \"email\": \"john@example.com\",
 *   \"phone\": \"+79991234567\",
 *   \"password\": \"Password123!\",
 *   \"firstName\": \"Иван\",
 *   \"lastName\": \"Иванов\",
 *   \"middleName\": \"Иванович\"
 * }
 */
@Builder
public record SignInRequest (
        @NotNull
        @Phone
        String phone,

        @Email
        String email,

        @Password
        String password
) { }