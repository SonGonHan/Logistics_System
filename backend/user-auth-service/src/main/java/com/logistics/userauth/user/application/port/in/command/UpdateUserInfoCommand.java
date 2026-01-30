package com.logistics.userauth.user.application.port.in.command;

import lombok.Builder;

/**
 * Команда (command) для сценария обновления профиля пользователя.
 *
 * <h2>Назначение</h2>
 * Содержит набор данных, которые могут быть обновлены в профиле пользователя, включая опциональную смену пароля.
 *
 * <h2>Поля</h2>
 * <ul>
 *   <li>{@code userId} — идентификатор пользователя.</li>
 *   <li>{@code email}, {@code phone}, {@code firstName}, {@code lastName}, {@code middleName} — данные профиля.</li>
 *   <li>{@code oldPassword}, {@code newPassword} — используются только если выполняется смена пароля.</li>
 * </ul>
 */
@Builder
public record UpdateUserInfoCommand (
        Long userId,
        String email,
        String phone,
        String firstName,
        String lastName,
        String middleName,
        String oldPassword,
        String newPassword,
        String ipAddress,
        String userAgent
) {
}