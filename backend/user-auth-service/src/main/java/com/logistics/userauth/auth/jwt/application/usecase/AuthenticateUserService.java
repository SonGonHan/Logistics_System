package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.exception.AuthenticationFailedException;
import com.logistics.userauth.auth.jwt.application.port.in.AuthenticateUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Сервис для аутентификации пользователя.
 *
 * <h2>Процесс</h2>
 * 1. Находит пользователя по телефону
 * 2. Проверяет пароль используя PasswordEncoder
 * 3. Генерирует access token
 * 4. Создает refresh token
 * 5. Логирует успешную аутентификацию в audit log
 * 6. Возвращает оба токена в ответе
 *
 * <h2>Исключения</h2>
 * - AuthenticationFailedException: Если телефон не найден или пароль неверен
 *
 * @implements AuthenticateUserUseCase
 */
@Service
@RequiredArgsConstructor
public class AuthenticateUserService implements AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;
    private final CreateAuditLogUseCase createAuditLogUseCase;

    /**
     * Выполняет аутентификацию пользователя по номеру телефона и паролю и возвращает пару токенов (access/refresh).
     *
     * <p>Алгоритм:</p>
     * <ul>
     *   <li>Ищет пользователя по телефону.</li>
     *   <li>Проверяет пароль через {@link PasswordEncoder}.</li>
     *   <li>Генерирует access token.</li>
     *   <li>Создаёт refresh token через внутренний use case создания сессии (привязка к IP и User-Agent).</li>
     * </ul>
     *
     * @param command Команда аутентификации (phone, password, ipAddress, userAgent).
     * @return DTO с accessToken и refreshToken.
     * @throws AuthenticationFailedException
     *         Если пользователь не найден или пароль неверный (сообщение намеренно общее).
     * @see AuthenticateUserUseCase
     * @see InternalCreateRefreshTokenUseCase
     */
    @Override
    public JwtAuthenticationResponse authenticate(AuthenticateUserCommand command) {
        var normalizedPhone = PhoneUtils.normalize(command.phone());

        var user = userRepository.findByPhone(normalizedPhone)
                .orElseThrow(() -> new AuthenticationFailedException(
                        normalizedPhone,
                        command.ipAddress(),
                        command.userAgent()
                ));

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new AuthenticationFailedException(
                    normalizedPhone,
                    command.ipAddress(),
                    command.userAgent()
            );
        }

        var accessToken = tokenGenerator.generateAccessToken(user);

        var refreshToken = createRefreshTokenUseCase.create(
                CreateRefreshTokenCommand.builder()
                        .userId(user.getId())
                        .ipAddress(command.ipAddress())
                        .userAgent(command.userAgent())
                        .build()
        );

        // Audit: USER_LOGIN_SUCCESS
        createAuditLogUseCase.create(new CreateAuditLogCommand(
                user.getId(),
                "USER_LOGIN_SUCCESS",
                user.getPhone(),
                command.ipAddress(),
                command.userAgent(),
                Map.of("userId", user.getId()),
                null,
                null
        ));

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }
}
