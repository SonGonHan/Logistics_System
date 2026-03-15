package com.logistics.corebusiness.acceptance.application.exception;

/**
 * Исключение валидации бизнес-правил приёмки.
 *
 * <h2>Использование</h2>
 * Используется для ошибок бизнес-валидации, например при некорректном ПВЗ или неактивном объекте.
 *
 * <h2>HTTP mapping</h2>
 * Должно быть обработано в {@code GlobalExceptionHandler} и преобразовано в HTTP 400 Bad Request.
 */
public class AcceptanceValidationException extends RuntimeException {

    public AcceptanceValidationException(String message) {
        super(message);
    }
}
