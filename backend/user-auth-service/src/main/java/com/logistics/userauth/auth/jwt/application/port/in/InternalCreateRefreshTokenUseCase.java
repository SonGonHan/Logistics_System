package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.usecase.InternalCreateRefreshTokenService;

/**
 * Internal Use Case для создания refresh токенов.
 *
 * <h2>Назначение</h2>
 * Отмечен как \"Internal\" потому что:
 * - Не должен вызваться напрямую из контроллеров
 * - Используется другими use cases (Register, Authenticate, Refresh)
 * - Инкапсулирует логику создания и сохранения сессии
 *
 * <h2>Процесс</h2>
 * 1. Генерирует случайный UUID для refresh токена
 * 2. Создает сессию пользователя с TTL
 * 3. Привязывает к IP и User-Agent (для защиты)
 * 4. Сохраняет в БД
 * 5. Возвращает токен строку
 *
 * <h2>Реализация</h2>
 * InternalCreateRefreshTokenService
 *
 * @see InternalCreateRefreshTokenService для реализации
 */
public interface InternalCreateRefreshTokenUseCase {
    String create(CreateRefreshTokenCommand command);
}
