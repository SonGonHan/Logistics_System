package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.session.application.port.out.UserSessionRepository;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.application.port.out.UserRepository;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Внутренний сервис для создания refresh токенов.
 *
 * <h2>Назначение</h2>
 * Отмечен как \"Internal\" потому что:
 * - Не должен вызваться напрямую из контроллеров
 * - Используется другими use cases (Register, Authenticate, Refresh)
 * - Инкапсулирует логику создания и сохранения сессии
 *
 * <h2>Каждый refresh token уникален и привязан к</h2>
 * - Конкретному пользователю
 * - Конкретному устройству (IP + User-Agent)
 * - Определенному времени истечения (TTL из конфигурации)
 *
 * <h2>Конфигурация</h2>
 * TTL читается из app.jwt.refresh-expiration в application.yml
 *
 * @implements InternalCreateRefreshTokenUseCase
 */
@Service
@RequiredArgsConstructor
public class InternalCreateRefreshTokenService implements InternalCreateRefreshTokenUseCase {

    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CreateAuditLogUseCase createAuditLogUseCase;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenTtlSeconds;

    /**
     * Внутренний use case: создаёт новый refresh token и сохраняет пользовательскую сессию.
     *
     * <p>Особенности:</p>
     * <ul>
     *   <li>refresh token генерируется как UUID;</li>
     *   <li>expiresAt вычисляется как now + refreshTokenTtlSeconds;</li>
     *   <li>сессия привязывается к IP (если передан) и User-Agent;</li>
     *   <li>revoked устанавливается в false.</li>
     * </ul>
     *
     * @param command Команда создания refresh token (userId, ipAddress, userAgent).
     * @return Сгенерированный refresh token (строка UUID).
     * @throws RuntimeException Если пользователь по userId не найден.
     * @see InternalCreateRefreshTokenUseCase
     * @see UserSession
     */
    @Override
    public String create(CreateRefreshTokenCommand command) {
        var user = userRepository.findById(command.userId()).orElseThrow(() ->  new RuntimeException("User not found"));

        String refreshToken = UUID.randomUUID().toString();

        var session = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenTtlSeconds))
                .createdAt(LocalDateTime.now())
                .ipAddress(command.ipAddress() != null ? new Inet(command.ipAddress()): null)
                .userAgent(command.userAgent())
                .revoked(false)
                .build();

        sessionRepository.save(session);

        // Audit: SESSION_CREATE
        createAuditLogUseCase.create(new CreateAuditLogCommand(
                user.getId(),
                "SESSION_CREATE",
                user.getPhone(),
                command.ipAddress(),
                command.userAgent(),
                Map.of(
                        "sessionId", session.getId(),
                        "expiresAt", session.getExpiresAt().toString(),
                        "deviceInfo", command.userAgent() != null ? command.userAgent() : "unknown"
                ),
                "user_sessions",
                session.getId()
        ));

        return refreshToken;
    }
}
