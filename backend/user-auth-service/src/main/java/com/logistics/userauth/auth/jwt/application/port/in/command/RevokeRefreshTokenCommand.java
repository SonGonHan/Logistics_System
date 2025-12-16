package com.logistics.userauth.auth.jwt.application.port.in.command;

import com.logistics.userauth.auth.jwt.adapter.in.web.AuthController;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.RefreshTokenRequest;
import com.logistics.userauth.auth.jwt.application.port.in.RevokeRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.usecase.RevokeRefreshTokenService;
import lombok.Builder;

/**
 * Команда для отзыва (revoke) refresh токена.
 *
 * Используется при выходе пользователя из системы (logout).
 * После отзыва токен помечается как revoked=true и не может быть использован снова.
 *
 * Инкапсулирует:
 * - refreshToken: токен, который нужно отозвать
 *
 * Используется в: RevokeRefreshTokenService
 * Создается в: AuthController из RefreshTokenRequest
 *
 * Пример создания:
 * ```java
 * RevokeRefreshTokenCommand command = RevokeRefreshTokenCommand.builder()
 *     .refreshToken("550e8400-e29b-41d4-a716-446655440000")
 *     .build();
 * ```
 *
 * @see RevokeRefreshTokenUseCase
 * @see RevokeRefreshTokenService
 * @see AuthController#logout(RefreshTokenRequest)
 */
@Builder
public record RevokeRefreshTokenCommand(String refreshToken) {
}
