package com.logistics.userauth.common.web;

import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
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
 * Глобальный обработчик исключений для всех REST endpoints.
 *
 * <h2>Назначение</h2>
 * Перехватывает исключения и возвращает единообразный JSON формат ошибок.
 *
 * <h2>Обработанные исключения</h2>
 * - BadCredentialsException → 401 INVALID_CREDENTIALS
 * - DataIntegrityViolationException → 409 CONFLICT
 * - MethodArgumentNotValidException → 400 VALIDATION_FAILED
 * - InvalidRefreshTokenException → 401 INVALID_REFRESH_TOKEN
 * - Все остальные Exception → 500 INTERNAL_SERVER_ERROR
 *
 * <h2>Формат ответа</h2>
 * {
 *   \"error\": \"ERROR_CODE\",
 *   \"message\": \"Human-readable message\",
 *   \"fields\": { \"fieldName\": \"error message\" }  // только для VALIDATION_FAILED
 * }
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок аутентификации (неверные учетные данные).
     *
     * @param ex BadCredentialsException
     * @return ResponseEntity с кодом 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_CREDENTIALS");
        body.put("message", "Неверный телефон или пароль");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Обработка ошибок целостности данных (duplicate keys, constraint violations).
     *
     * @param ex DataIntegrityViolationException
     * @return ResponseEntity с кодом 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "CONFLICT");
        body.put("message", "Пользователь с таким телефоном или email уже существует");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Обработка ошибок валидации входных параметров.
     *
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity с кодом 400 и деталями ошибок по полям
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
     * Обработка ошибок невалидного refresh token.
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
}
