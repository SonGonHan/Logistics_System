package com.logistics.corebusiness.common.web;

import com.logistics.corebusiness.waybill.application.exception.DraftAccessDeniedException;
import com.logistics.corebusiness.waybill.application.exception.DraftInvalidStatusException;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.exception.DraftValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST endpoints core-business-service.
 *
 * <h2>Обработка исключений</h2>
 * Возвращает JSON с детализацией ошибки в единообразном формате.
 *
 * <h2>Типы исключений</h2>
 * <ul>
 *   <li>DraftNotFoundException → 404 (NOT_FOUND)</li>
 *   <li>DraftAccessDeniedException → 403 (FORBIDDEN)</li>
 *   <li>DraftInvalidStatusException → 409 (CONFLICT)</li>
 *   <li>DraftValidationException → 400 (BAD_REQUEST)</li>
 *   <li>MethodArgumentNotValidException → 400 (VALIDATION_FAILED)</li>
 *   <li>DataIntegrityViolationException → 409 (CONFLICT)</li>
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

    /**
     * Обработка исключения "черновик не найден".
     *
     * @param ex DraftNotFoundException
     * @return ResponseEntity с кодом 404
     */
    @ExceptionHandler(DraftNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDraftNotFound(DraftNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "DRAFT_NOT_FOUND");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Обработка исключения "доступ запрещен".
     *
     * @param ex DraftAccessDeniedException
     * @return ResponseEntity с кодом 403
     */
    @ExceptionHandler(DraftAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleDraftAccessDenied(DraftAccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "ACCESS_DENIED");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Обработка исключения "недопустимый статус черновика".
     *
     * @param ex DraftInvalidStatusException
     * @return ResponseEntity с кодом 409
     */
    @ExceptionHandler(DraftInvalidStatusException.class)
    public ResponseEntity<Map<String, Object>> handleDraftInvalidStatus(DraftInvalidStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_DRAFT_STATUS");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Обработка исключения валидации бизнес-правил.
     *
     * @param ex DraftValidationException
     * @return ResponseEntity с кодом 400
     */
    @ExceptionHandler(DraftValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDraftValidation(DraftValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "VALIDATION_ERROR");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Обработка ошибок валидации Bean Validation (@Valid, @NotNull, etc.).
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
     * Обработка нарушения ограничений БД (duplicate keys, constraint violations).
     *
     * @param ex DataIntegrityViolationException
     * @return ResponseEntity с кодом 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "CONFLICT");

//        String message = "Нарушение уникальности данных";
//        if (ex.getMessage() != null) {
//            if (ex.getMessage().contains("barcode")) {
//                message = "Черновик с таким barcode уже существует";
//            }
//        }

//        body.put("message", message);
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Обработка всех остальных исключений (fallback).
     *
     * @param ex Exception
     * @return ResponseEntity с кодом 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INTERNAL_SERVER_ERROR");
//        body.put("message", "Произошла внутренняя ошибка сервера");
//        body.put("details", ex.getMessage());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
