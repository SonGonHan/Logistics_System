package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.RefreshAccessTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.RefreshAccessTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.auth.session.application.port.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Сервис для обновления access токена с использованием refresh токена.
 *
 * <h2>Реализует паттерн Token Rotation</h2>
 * 1. Принимает старый refresh token
 * 2. Проверяет его валидность (не истек, не отозван)
 * 3. Помечает старый refresh token как отозванный
 * 4. Выдает новый access token и новый refresh token
 *
 * Это повышает безопасность: если refresh token будет скомпрометирован,
 * злоумышленник сможет его использовать только один раз.
 *
 * <h2>Валидация</h2>
 * Проверяет:
 * - Токен существует в БД
 * - Токен не был отозван (revoked = false)
 * - Токен не истек (expiresAt >= now)
 *
 * <h2>Исключения</h2>
 * - InvalidRefreshTokenException: Если токен невалиден, отозван или истек
 *
 * @implements RefreshAccessTokenUseCase
 * @Transactional используется для атомарности операции
 */
@Service
@RequiredArgsConstructor
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final UserSessionRepository repository;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;
    private final CreateAuditLogUseCase createAuditLogUseCase;

    /**
     * Обновляет access token по refresh token и выдаёт новую пару токенов (Token Rotation).
     *
     * <p>Алгоритм Token Rotation:</p>
     * <ol>
     *   <li>Находит сессию по refresh token.</li>
     *   <li>Проверяет, что токен не отозван и не истёк.</li>
     *   <li>Помечает текущую сессию как revoked=true (старый refresh становится недействительным).</li>
     *   <li>Генерирует новый access token.</li>
     *   <li>Создаёт новый refresh token (новая сессия) с привязкой к текущим IP и User-Agent.</li>
     * </ol>
     *
     * @param command Команда обновления (refreshToken, ipAddress, userAgent).
     * @return DTO с новым accessToken и новым refreshToken.
     * @throws InvalidRefreshTokenException
     *         Если refresh token не найден, отозван или истёк.
     * @see RefreshAccessTokenUseCase
     * @see UserSessionRepository
     */
    @Override
    @Transactional
    public JwtAuthenticationResponse refresh(RefreshAccessTokenCommand command) {
        var session = repository.findByRefreshToken(command.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        validate(session);

        String newAccessToken = tokenGenerator.generateAccessToken(session.getUser());

        session.setRevoked(true);
        repository.save(session);

        // Audit: TOKEN_REFRESH
        createAuditLogUseCase.create(new CreateAuditLogCommand(
                session.getUser().getId(),
                "TOKEN_REFRESH",
                session.getUser().getPhone(),
                command.ipAddress(),
                command.userAgent(),
                Map.of(
                        "sessionId", session.getId(),
                        "refreshedAt", LocalDateTime.now().toString()
                ),
                null,
                null
        ));

        String newRefreshToken = createRefreshTokenUseCase.create(
                CreateRefreshTokenCommand.builder()
                        .userId(session.getUser().getId())
                        .ipAddress(command.ipAddress())
                        .userAgent(command.userAgent())
                        .build()
        );
        return new JwtAuthenticationResponse(newAccessToken, newRefreshToken);
    }

    /**
     * Проверяет валидность найденной пользовательской сессии для refresh token.
     *
     * @param session Сессия пользователя, найденная по refresh token.
     * @throws InvalidRefreshTokenException
     *         Если сессия помечена как revoked=true или если expiresAt меньше текущего времени.
     */
    private static void validate(UserSession session) {
        if (session.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token is revoked");
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token is expired");
        }
    }
}
