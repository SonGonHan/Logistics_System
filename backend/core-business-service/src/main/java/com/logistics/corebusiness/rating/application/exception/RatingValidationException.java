package com.logistics.corebusiness.rating.application.exception;

/**
 * Исключение валидации бизнес-правил рейтинга.
 *
 * <h2>Использование</h2>
 * - Накладная не в статусе DELIVERED
 * - Некорректное значение оценки
 *
 * <h2>HTTP mapping</h2>
 * Обрабатывается в GlobalExceptionHandler -> HTTP 400 Bad Request.
 */
public class RatingValidationException extends RuntimeException {

    public RatingValidationException(String message) {
        super(message);
    }

    public static RatingValidationException waybillNotDelivered(Long waybillId, String currentStatus) {
        return new RatingValidationException(
                "Cannot rate waybill " + waybillId + ": status is " + currentStatus + ", expected DELIVERED");
    }
}
