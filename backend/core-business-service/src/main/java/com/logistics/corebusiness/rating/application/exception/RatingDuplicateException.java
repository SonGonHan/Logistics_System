package com.logistics.corebusiness.rating.application.exception;

/**
 * Исключение при попытке повторного создания рейтинга.
 *
 * <h2>HTTP mapping</h2>
 * Обрабатывается в GlobalExceptionHandler -> HTTP 409 Conflict.
 */
public class RatingDuplicateException extends RuntimeException {

    public RatingDuplicateException(String message) {
        super(message);
    }

    public static RatingDuplicateException forWaybill(Long waybillId) {
        return new RatingDuplicateException(
                "Rating already exists for waybill id: " + waybillId);
    }
}
