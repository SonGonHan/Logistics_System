package com.logistics.corebusiness.waybill.application.exception;

/**
 * Исключение валидации бизнес-правил для накладных.
 *
 * <h2>HTTP mapping</h2>
 * Обрабатывается в GlobalExceptionHandler -> HTTP 400 Bad Request.
 */
public class WaybillValidationException extends RuntimeException {

    public WaybillValidationException(String message) {
        super(message);
    }
}
