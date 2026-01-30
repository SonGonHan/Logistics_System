package com.logistics.userauth.auth.jwt.adapter.in.web.dto;

import lombok.Builder;

/**
 * Ответ на запрос проверки типа пользователя.
 *
 * <h2>Назначение</h2>
 * Информирует UI о том, какой метод аутентификации использовать:
 * - isClient = true → отправить OTP код (passwordless)
 * - isClient = false → показать поле пароля
 *
 * <h2>Поля</h2>
 * - userExists: Существует ли пользователь с таким телефоном/email
 * - isClient: Является ли пользователь клиентом (или будет создан как клиент)
 *
 * <h2>Примеры ответов</h2>
 * Клиент существует:
 * {
 *   "userExists": true,
 *   "isClient": true
 * }
 *
 * Сотрудник существует:
 * {
 *   "userExists": true,
 *   "isClient": false
 * }
 *
 * Пользователь не найден (новый клиент):
 * {
 *   "userExists": false,
 *   "isClient": true
 * }
 */
@Builder
public record CheckUserTypeResponse(
        boolean userExists,
        boolean isClient
) { }
