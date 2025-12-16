package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.RefreshAccessTokenCommand;
import com.logistics.userauth.auth.jwt.application.usecase.RefreshAccessTokenService;

/**
 * Use Case для обновления access токена используя refresh token.
 *
 * <h2>Процесс (Token Rotation)</h2>
 * 1. Получает старый refresh token
 * 2. Проверяет его валидность
 * 3. Помечает старый токен как отозванный
 * 4. Выдает новый access token и новый refresh token
 *
 * Это повышает безопасность: если refresh token будет скомпрометирован,
 * злоумышленник сможет его использовать только один раз.
 *
 * <h2>Реализация</h2>
 * RefreshAccessTokenService
 *
 * @see RefreshAccessTokenService для реализации
 */
public interface RefreshAccessTokenUseCase {
    JwtAuthenticationResponse refresh(RefreshAccessTokenCommand command);
}
