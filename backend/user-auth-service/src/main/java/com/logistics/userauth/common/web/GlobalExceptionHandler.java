package com.logistics.userauth.common.web;

import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.userauth.auth.jwt.application.exception.AuthenticationFailedException;
import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException;
import com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.notification.common.application.exception.RateLimitExceededException;
import com.logistics.userauth.notification.email.application.exception.EmailDeliveryException;
import com.logistics.userauth.notification.sms.application.exception.SmsDeliveryException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST endpoints.
 *
 * <h2>Обработка исключений</h2>
 * Возвращает JSON с детализацией ошибки.
 *
 * <h2>Типы исключений</h2>
 * <ul>
 *   <li>AuthenticationFailedException → 401 (INVALID_CREDENTIALS) + audit log</li>
 *   <li>BadCredentialsException → 401 (INVALID_CREDENTIALS)</li>
 *   <li>DataIntegrityViolationException → 409 (CONFLICT)</li>
 *   <li>MethodArgumentNotValidException → 400 (VALIDATION_FAILED)</li>
 *   <li>InvalidRefreshTokenException → 401 (INVALID_REFRESH_TOKEN)</li>
 *   <li>Exception → 500 (INTERNAL_SERVER_ERROR)</li>
 * </ul>
 *
 * <h2>Формат ответа</h2>
 * <pre>
 * {
 *   "error": "ERROR_CODE",
 *   "message": "Human-readable message",
 *   "fields": { "fieldName": "error message" } // только для VALIDATION_FAILED
 * }
 * </pre>
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final CreateAuditLogUseCase createAuditLogUseCase;

    /**
     * Обработка неудачной аутентификации с audit logging.
     *
     * @param ex AuthenticationFailedException
     * @return ResponseEntity с кодом 401
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailed(
            AuthenticationFailedException ex) {

        // Audit: USER_LOGIN_FAILURE
        createAuditLogUseCase.create(new CreateAuditLogCommand(
                null, // Неизвестный пользователь
                "USER_LOGIN_FAILURE",
                ex.getAttemptedPhone(),
                ex.getIpAddress(),
                ex.getUserAgent(),
                Map.of(
                        "attemptedPhone", ex.getAttemptedPhone(),
                        "reason", "INVALID_CREDENTIALS"
                ),
                null,
                null
        ));

        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_CREDENTIALS");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Обработка ошибок аутентификации (общий случай).
     *
     * @param ex BadCredentialsException
     * @return ResponseEntity с кодом 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_CREDENTIALS");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Обработка нарушения ограничений БД (duplicate keys, constraint violations).
     *
     * @param ex DataIntegrityViolationException
     * @return ResponseEntity с кодом 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "CONFLICT");

        // Извлекаем более детальное сообщение, если возможно
        String message = "Нарушение уникальности данных";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("email")) {
                message = "Пользователь с таким email уже существует";
            } else if (ex.getMessage().contains("phone")) {
                message = "Пользователь с таким телефоном уже существует";
            }
        }

        body.put("message", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Обработка ошибок валидации Bean Validation.
     *
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity с кодом 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("error", "VALIDATION_FAILED");
        body.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Обработка невалидного refresh token.
     *
     * @param ex InvalidRefreshTokenException
     * @return ResponseEntity с кодом 401
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_REFRESH_TOKEN");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Обработка превышения rate limit.
     *
     * @param ex RateLimitExceededException
     * @return ResponseEntity с кодом 429
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "RATE_LIMIT_EXCEEDED");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(body);
    }

    /**
     * Обработка ошибок доставки SMS
     *
     * @param ex SmsDeliveryException
     * @return ResponseEntity с кодом 503
     */
    @ExceptionHandler(SmsDeliveryException.class)
    public ResponseEntity<Map<String, Object>> handleSmsDeliveryError(SmsDeliveryException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "SMS_DELIVERY_FAILED");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    /**
     * Обработка ошибок доставки Email
     *
     * @param ex EmailDeliveryException
     * @return ResponseEntity с кодом 503
     */
    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDeliveryError(EmailDeliveryException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "EMAIL_DELIVERY_FAILED");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    /**
     * Обработка неверного кода верификации.
     *
     * @param ex InvalidVerificationCodeException
     * @return ResponseEntity с кодом 400
     */
    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidVerificationCode(InvalidVerificationCodeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_VERIFICATION_CODE");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Обработка ошибки неподтверждённого телефона.
     *
     * @param ex PhoneNotVerifiedException
     * @return ResponseEntity с кодом 400
     */
    @ExceptionHandler(PhoneNotVerifiedException.class)
    public ResponseEntity<Map<String, Object>> handlePhoneNotVerified(PhoneNotVerifiedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "PHONE_NOT_VERIFIED");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
