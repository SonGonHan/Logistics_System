package com.logistics.userauth.auth.jwt.application.port.in.command;

import com.logistics.userauth.auth.jwt.adapter.in.web.AuthController;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.RefreshTokenRequest;
import com.logistics.userauth.auth.jwt.application.port.in.RefreshAccessTokenUseCase;
import com.logistics.userauth.auth.jwt.application.usecase.RefreshAccessTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;

/**
 * Команда для обновления access токена через refresh токен.
 *
 * Реализует паттерн Token Rotation:
 * - Использует старый refresh token для получения новой пары токенов
 * - Старый refresh token отзывается после использования
 * - Новый refresh token привязывается к текущему IP и User-Agent
 *
 * Инкапсулирует:
 * - refreshToken: текущий valid refresh token
 * - ipAddress: IP-адрес клиента (для привязки новой сессии)
 * - userAgent: информация об устройстве клиента
 *
 * Используется в: RefreshAccessTokenService
 * Создается в: AuthController из RefreshTokenRequest
 *
 * Пример создания:
 * ```java
 * RefreshAccessTokenCommand command = RefreshAccessTokenCommand.builder()
 *     .refreshToken("550e8400-e29b-41d4-a716-446655440000")
 *     .ipAddress("192.168.1.1")
 *     .userAgent("Mozilla/5.0")
 *     .build();
 * ```
 *
 * @see RefreshAccessTokenUseCase
 * @see RefreshAccessTokenService
 * @see AuthController#refresh(RefreshTokenRequest, HttpServletRequest)
 * @see <a href="https://auth0.com/blog/refresh-tokens-what-are-they-and-when-to-use-them/">Token Rotation Pattern</a>
 */
@Builder
public record RefreshAccessTokenCommand (
        String refreshToken,
        String ipAddress,
        String userAgent) {
}
