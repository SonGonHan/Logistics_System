package com.logistics.userauth.auth.jwt.application.port.in.command;

import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.usecase.AuthenticateUserService;
import com.logistics.userauth.auth.jwt.application.usecase.InternalCreateRefreshTokenService;
import com.logistics.userauth.auth.jwt.application.usecase.RegisterUserService;
import lombok.Builder;

/**
 * Внутренняя команда для создания нового refresh токена.
 *
 * Используется только внутри сервисов аутентификации (не вызывается напрямую из контроллеров).
 * Создает новую сессию пользователя с привязкой к IP и устройству.
 *
 * Инкапсулирует:
 * - userId: ID пользователя, для которого создается сессия
 * - ipAddress: IP-адрес клиента для привязки сессии
 * - userAgent: информация об устройстве клиента
 *
 * Используется в: InternalCreateRefreshTokenService
 * Создается в: AuthenticateUserService, RegisterUserService, RefreshAccessTokenService
 *
 * Пример создания:
 * ```java
 * CreateRefreshTokenCommand command = CreateRefreshTokenCommand.builder()
 *     .userId(1L)
 *     .ipAddress("192.168.1.1")
 *     .userAgent("Mozilla/5.0")
 *     .build();
 * ```
 *
 * @see InternalCreateRefreshTokenUseCase
 * @see InternalCreateRefreshTokenService
 * @see AuthenticateUserService
 * @see RegisterUserService
 */
@Builder
public record CreateRefreshTokenCommand(
        Long userId,
        String ipAddress,
        String userAgent
){
}
