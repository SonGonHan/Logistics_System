package com.logistics.corebusiness.rating.application.exception;

/**
 * Исключение, выбрасываемое когда рейтинг не найден.
 *
 * <h2>HTTP mapping</h2>
 * Обрабатывается в GlobalExceptionHandler -> HTTP 404 Not Found.
 */
public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException(String message) {
        super(message);
    }

    public static RatingNotFoundException byWaybillId(Long waybillId) {
        return new RatingNotFoundException("Rating not found for waybill id: " + waybillId);
    }

    public static RatingNotFoundException byId(Long id) {
        return new RatingNotFoundException("Rating not found with id: " + id);
    }
}
