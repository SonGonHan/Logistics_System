package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.application.port.in.command.RevokeRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.usecase.RevokeRefreshTokenService;

/**
 * Use Case для отзыва (revoke) refresh токенов.
 *
 * <h2>Назначение</h2>
 * Отзыв токена делает его непригодным для дальнейшего использования.
 * Используется при logout и других операциях завершения сессии.
 *
 * <h2>Процесс</h2>
 * 1. Получает refresh token
 * 2. Находит соответствующую сессию
 * 3. Помечает сессию как revoked = true
 *
 * При попытке использовать отозванный токен для обновления access token
 * будет выброшено исключение InvalidRefreshTokenException.
 *
 * <h2>Реализация</h2>
 * RevokeRefreshTokenService
 *
 * @see RevokeRefreshTokenService для реализации
 */
public interface RevokeRefreshTokenUseCase {
    void revoke(RevokeRefreshTokenCommand command);
}
