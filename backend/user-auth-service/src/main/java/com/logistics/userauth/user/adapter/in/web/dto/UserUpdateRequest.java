package com.logistics.userauth.user.adapter.in.web.dto;

import com.logistics.shared.validation.Password;
import com.logistics.shared.validation.Phone;
import jakarta.validation.constraints.Email;
import lombok.Builder;

/**
 * DTO запроса на обновление данных пользователя (профиля).
 *
 * <h2>Валидация</h2>
 * <ul>
 *   <li>{@code email} валидируется аннотацией {@link jakarta.validation.constraints.Email}.</li>
 *   <li>{@code phone} валидируется кастомной аннотацией {@link com.logistics.shared.validation.Phone}.</li>
 *   <li>{@code oldPassword}/{@code newPassword} валидируются аннотацией {@link com.logistics.shared.validation.Password}.</li>
 * </ul>
 *
 * <h2>Семантика смены пароля</h2>
 * Если {@code newPassword} передан, сервис обновления ожидает также {@code oldPassword}
 * и проверяет его соответствие сохранённому паролю пользователя.
 */
@Builder
public record UserUpdateRequest(
        @Email
        String email,
        @Phone
        String phone,
        String firstName,
        String lastName,
        String middleName,
        @Password
        String oldPassword,
        @Password
        String newPassword
) {
}
