package com.logistics.userauth.auth.jwt.application.exception;

import com.logistics.userauth.auth.jwt.application.usecase.RefreshAccessTokenService;
import com.logistics.userauth.common.web.GlobalExceptionHandler;

/**
 * Исключение выбрасываемое когда refresh token невалиден или не может быть использован.
 *
 * <h2>Случаи когда выбрасывается</h2>
 * 1. Token not found: Токен отсутствует в БД
 * 2. Token expired: Токен истек (current time > expiresAt)
 * 3. Token revoked: Токен был явно отозван (revoked = true)
 * 4. Token rotated: Токен был заменен на новый (pattern token rotation)
 *
 * <h2>HTTP ответ</h2>
 * 401 Unauthorized
 *
 * <h2>Обработка</h2>
 * @ExceptionHandler в GlobalExceptionHandler возвращает:
 * {
 *   \"error\": \"INVALID_REFRESH_TOKEN\",
 *   \"message\": \"...\"
 * }
 *
 * @see GlobalExceptionHandler для обработки этого исключения
 * @see RefreshAccessTokenService где выбрасывается
 */
public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
