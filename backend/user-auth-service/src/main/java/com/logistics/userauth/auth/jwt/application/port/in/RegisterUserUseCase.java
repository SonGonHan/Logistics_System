package com.logistics.userauth.auth.jwt.application.port.in;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.auth.jwt.application.usecase.RegisterUserService;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;

/**
 * Use Case для регистрации нового пользователя.
 *
 * <h2>Процесс</h2>
 * 1. Получает команду с данными пользователя
 * 2. Проверяет уникальность телефона/email
 * 3. Создает новый аккаунт с ролью CLIENT
 * 4. Выдает access token и refresh token
 *
 * <h2>Реализация</h2>
 * RegisterUserService
 *
 * @see RegisterUserService для реализации
 */
public interface RegisterUserUseCase {
    JwtAuthenticationResponse register(RegisterUserCommand command);
}
