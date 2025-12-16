package com.logistics.userauth.auth.jwt.application.port.in.command;

import com.logistics.userauth.auth.jwt.adapter.in.web.AuthController;
import com.logistics.userauth.auth.jwt.application.port.in.AuthenticateUserUseCase;
import com.logistics.userauth.auth.jwt.application.usecase.AuthenticateUserService;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;


/**
 * Команда для аутентификации пользователя в системе.
 *
 * Инкапсулирует все необходимые данные для входа пользователя:
 * - phone: номер телефона (основной идентификатор)
 * - password: пароль в открытом виде (будет проверен через BCrypt)
 * - ipAddress: IP-адрес клиента для логирования и безопасности
 * - userAgent: браузер/устройство клиента для логирования
 *
 * Используется в: AuthenticateUserService
 * Создается в: AuthController из SignInRequest
 *
 * Пример создания:
 * ```java
 * AuthenticateUserCommand command = AuthenticateUserCommand.builder()
 *     .phone("+79991234567")
 *     .password("Password123!")
 *     .ipAddress("192.168.1.1")
 *     .userAgent("Mozilla/5.0")
 *     .build();
 * ```
 *
 * @see AuthenticateUserUseCase
 * @see AuthenticateUserService
 * @see AuthController#signIn(SignInRequest, HttpServletRequest)
 */
@Builder
public record AuthenticateUserCommand (
        String phone,
        String password,
        String ipAddress,
        String userAgent) {
}
