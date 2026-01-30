package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.CheckUserTypeResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.CheckUserTypeCommand;

/**
 * Use case для определения типа пользователя перед авторизацией.
 *
 * <h2>Назначение</h2>
 * Позволяет UI определить, какой метод аутентификации использовать:
 * - Клиенты (CLIENT) → passwordless (OTP код)
 * - Сотрудники (все остальные роли) → пароль
 * - Новые пользователи → автоматически считаются клиентами
 *
 * <h2>Логика</h2>
 * 1. Ищет пользователя по телефону или email (автоопределение формата)
 * 2. Если найден → возвращает userExists=true и определяет isClient
 * 3. Если не найден → возвращает userExists=false, isClient=true
 *
 * <h2>Примеры</h2>
 * - Существующий CLIENT: {userExists: true, isClient: true}
 * - Существующий DRIVER: {userExists: true, isClient: false}
 * - Новый пользователь: {userExists: false, isClient: true}
 *
 * @see CheckUserTypeCommand для входных параметров
 * @see CheckUserTypeResponse для формата ответа
 */
public interface CheckUserTypeUseCase {
    CheckUserTypeResponse check(CheckUserTypeCommand command);
}