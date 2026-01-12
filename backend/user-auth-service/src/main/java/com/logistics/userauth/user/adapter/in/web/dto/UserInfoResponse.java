package com.logistics.userauth.user.adapter.in.web.dto;

import lombok.Builder;

/**
 * DTO ответа с информацией о пользователе.
 *
 * <p>Используется в REST API как представление профиля пользователя без чувствительных данных
 * (например, без {@code passwordHash}).
 *
 * <h2>Поля</h2>
 * <ul>
 *   <li>{@code email} — email пользователя (может быть null).</li>
 *   <li>{@code phone} — телефон пользователя.</li>
 *   <li>{@code firstName}/{@code lastName}/{@code middleName} — ФИО.</li>
 * </ul>
 */
@Builder
public record UserInfoResponse(
    String email,
    String phone,
    String firstName,
    String lastName,
    String middleName
) {
}
