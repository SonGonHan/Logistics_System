package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.adapter.out.JwtTokenProvider;
import com.logistics.userauth.auth.jwt.application.port.in.AuthenticateUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.auth.session.domain.UserSession;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

/**
 * Сервис для аутентификации пользователя.
 *
 * <h2>Процесс</h2>
 * 1. Находит пользователя по телефону
 * 2. Проверяет пароль используя PasswordEncoder
 * 3. Генерирует access token
 * 4. Создает refresh token
 * 5. Возвращает оба токена в ответе
 *
 * <h2>Исключения</h2>
 * - BadCredentialsException: Если телефон не найден или пароль неверен
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
     * @throws BadCredentialsException
     *         Если пользователь не найден или пароль неверный (сообщение намеренно общее).
     * @see AuthenticateUserUseCase
     * @see InternalCreateRefreshTokenUseCase
     */
    @Override
    public JwtAuthenticationResponse authenticate(AuthenticateUserCommand command) {
        var normalizedPhone = PhoneUtils.normalize(command.phone());

        var user = userRepository.findByPhone(normalizedPhone)
                .orElseThrow(() -> new BadCredentialsException("Неверный телефон или пароль"));

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Неверный телефон или пароль");
        }

        var accessToken = tokenGenerator.generateAccessToken(user);

        var refreshToken = createRefreshTokenUseCase.create(
                CreateRefreshTokenCommand.builder()
                        .userId(user.getId())
                        .ipAddress(command.ipAddress())
                        .userAgent(command.userAgent())
                        .build()
        );
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }
}
