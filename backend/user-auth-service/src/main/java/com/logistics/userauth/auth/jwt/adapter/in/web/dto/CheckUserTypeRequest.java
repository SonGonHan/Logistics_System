package com.logistics.userauth.auth.jwt.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Запрос для проверки типа пользователя перед авторизацией.
 *
 * <h2>Назначение</h2>
 * Используется в адаптивной форме входа для определения,
 * какие поля показывать пользователю (OTP код или пароль).
 *
 * <h2>Валидация</h2>
 * - identifier: Не пустой. Может быть телефоном или email
 * - Система автоматически определит тип по формату
 *
 * <h2>Пример запроса</h2>
 * {
 *   "identifier": "+79991234567"
 * }
 * или
 * {
 *   "identifier": "john@example.com"
 * }
 */
@Builder
public record CheckUserTypeRequest(
        @NotBlank(message = "Укажите телефон или email")
        String identifier
) { }
