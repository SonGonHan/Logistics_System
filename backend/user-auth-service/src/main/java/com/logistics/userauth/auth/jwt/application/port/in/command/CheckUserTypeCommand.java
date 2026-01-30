package com.logistics.userauth.auth.jwt.application.port.in.command;

import lombok.Builder;

/**
 * Команда для проверки типа пользователя перед авторизацией.
 *
 * <h2>Назначение</h2>
 * Используется для определения метода аутентификации
 * (пароль для сотрудников, OTP для клиентов).
 *
 * <h2>Поля</h2>
 * - identifier: Телефон или email пользователя
 *
 * @see com.logistics.userauth.auth.jwt.application.port.in.CheckUserTypeUseCase
 */
@Builder
public record CheckUserTypeCommand(
        String identifier
) { }