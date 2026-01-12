package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.RegisterUserUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.CreateRefreshTokenCommand;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.sms.application.port.out.SmsRepository;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис для регистрации новых пользователей.
 *
 * <h2>Процесс</h2>
 * 1. Получает данные нового пользователя
 * 2. Проверяет уникальность телефона/email
 * 3. Хэширует пароль с помощью PasswordEncoder (BCrypt)
 * 4. Создает пользователя с ролью CLIENT и статусом ACTIVE
 * 5. Сохраняет в БД
 * 6. Генерирует access token
 * 7. Создает refresh token
 * 8. Возвращает оба токена
 *
 * <h2>Исключения</h2>
 * - DataIntegrityViolationException: Если телефон/email уже существует
 *
 * @implements RegisterUserUseCase
 */
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final SmsRepository smsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    /**
     * Регистрирует нового пользователя и возвращает пару токенов (access/refresh).
     *
     * <p>Алгоритм:</p>
     * <ul>
     *   <li>Хеширует пароль через {@link PasswordEncoder}.</li>
     *   <li>Создаёт доменного пользователя с ролью CLIENT и статусом ACTIVE.</li>
     *   <li>Сохраняет пользователя через {@link UserRepository}.</li>
     *   <li>Генерирует access token.</li>
     *   <li>Создаёт refresh token (новая сессия) с привязкой к IP и User-Agent.</li>
     * </ul>
     *
     * @param command Команда регистрации (email, phone, rawPassword, ФИО, ipAddress, userAgent).
     * @return DTO с accessToken и refreshToken.
     * @throws DataIntegrityViolationException
     *         При нарушении уникальности (например, телефон или email уже существуют) — обычно маппится в 409 обработчиком ошибок.
     * @see RegisterUserUseCase
     * @see InternalCreateRefreshTokenUseCase
     */
    @Override
    public JwtAuthenticationResponse register(RegisterUserCommand command) {
        validatePhoneVerification(command.phone());

        var user = User.builder()
                .email(command.email())
                .phone(command.phone())
                .passwordHash(passwordEncoder.encode(command.rawPassword()))
                .firstName(command.firstName())
                .lastName(command.lastName())
                .middleName(command.middleName())
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .lastAccessedTime(LocalDateTime.now())
                .build();

        var saved = userRepository.save(user);

        smsRepository.deleteVerificationStatus(command.phone());

        var accessToken = tokenGenerator.generateAccessToken(saved);

        var refreshToken = createRefreshTokenUseCase.create(
                CreateRefreshTokenCommand.builder()
                        .userId(saved.getId())
                        .ipAddress(command.ipAddress())
                        .userAgent(command.userAgent())
                        .build()
        );
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    private void validatePhoneVerification(String phone) {
        if (!smsRepository.isPhoneVerified(phone)) {
            throw new PhoneNotVerifiedException(
                    "Необходимо подтвердить номер телефона перед регистрацией"
            );
        }
    }
}
