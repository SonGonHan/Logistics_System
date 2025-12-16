package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.usecase.AuthenticateUserService;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;

/**
 * Use Case для аутентификации пользователя (вход в систему).
 *
 * <h2>Процесс</h2>
 * 1. Получает команду с телефоном и паролем
 * 2. Проверяет учетные данные
 * 3. Выдает access token и refresh token
 *
 * <h2>Реализация</h2>
 * AuthenticateUserService
 *
 * @see AuthenticateUserService для реализации
 */
public interface AuthenticateUserUseCase {
    JwtAuthenticationResponse authenticate(AuthenticateUserCommand command);
}
