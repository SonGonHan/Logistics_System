package com.logistics.corebusiness.waybill.application.exception;

/**
 * Исключение, выбрасываемое при нарушении бизнес-правил валидации черновика.
 *
 * <h2>Сценарии</h2>
 * - Не указан ни id, ни barcode при поиске
 * - Указаны оба параметра (id и barcode) одновременно
 * - Отрицательный вес или габариты
 * - Несоответствие правилам тарификации
 *
 * <h2>Отличие от Jakarta Validation</h2>
 * Jakarta Validation (@NotNull, @DecimalMin) - для структурной валидации DTO.
 * DraftValidationException - для бизнес-правил на уровне application layer.
 *
 * <h2>HTTP mapping</h2>
 * Должно быть обработано в @RestControllerAdvice и преобразовано в HTTP 400 Bad Request.
 */
public class DraftValidationException extends RuntimeException {

    public DraftValidationException(String message) {
        super(message);
    }
}
