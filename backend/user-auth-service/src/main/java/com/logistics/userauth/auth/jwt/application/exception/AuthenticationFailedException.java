package com.logistics.userauth.auth.jwt.application.exception;

import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Исключение для неудачных попыток аутентификации.
 *
 * <h2>Назначение</h2>
 * Расширяет стандартное {@link BadCredentialsException} дополнительными данными для аудит логирования:
 * - attemptedPhone: Телефон, с которым пытались войти
 * - ipAddress: IP-адрес клиента
 * - userAgent: User-Agent браузера
 *
 * <h2>Отличие от BadCredentialsException</h2>
 * Стандартное исключение Spring Security не хранит контекст попытки входа.
 * Это исключение позволяет {@link com.logistics.userauth.common.web.GlobalExceptionHandler}
 * логировать неудачные попытки входа в audit_logs для security анализа.
 *
 * <h2>Обработка</h2>
 * @see com.logistics.userauth.common.web.GlobalExceptionHandler#handleAuthenticationFailed
 *
 * <h2>Использование</h2>
 * <pre>
 * if (!passwordEncoder.matches(password, user.getPasswordHash())) {
 *     throw new AuthenticationFailedException(
 *         normalizedPhone,
 *         command.ipAddress(),
 *         command.userAgent()
 *     );
 * }
 * </pre>
 */
@Getter
public class AuthenticationFailedException extends BadCredentialsException {

    private final String attemptedPhone;

    private final String ipAddress;

    private final String userAgent;

    /**
     * Создаёт исключение для неудачной аутентификации.
     *
     * @param phone       Телефон, с которым пытались войти
     * @param ipAddress   IP-адрес клиента
     * @param userAgent   User-Agent браузера
     */
    public AuthenticationFailedException(String phone, String ipAddress, String userAgent) {
        super("Неверный телефон или пароль");
        this.attemptedPhone = phone;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}