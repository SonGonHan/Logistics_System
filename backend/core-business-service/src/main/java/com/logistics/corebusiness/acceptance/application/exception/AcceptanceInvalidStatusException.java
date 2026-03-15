package com.logistics.corebusiness.acceptance.application.exception;

import com.logistics.corebusiness.waybill.domain.DraftStatus;

/**
 * Исключение, выбрасываемое при попытке принять черновик в недопустимом статусе.
 *
 * <h2>Использование</h2>
 * Применяется, когда операция приёмки разрешена только для черновиков со статусом {@code PENDING}.
 *
 * <h2>HTTP mapping</h2>
 * Должно быть обработано в {@code GlobalExceptionHandler} и преобразовано в HTTP 409 Conflict.
 */
public class AcceptanceInvalidStatusException extends RuntimeException {

    public AcceptanceInvalidStatusException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение для случая, когда черновик не находится в статусе PENDING.
     *
     * @param barcode штрих-код черновика
     * @param currentStatus текущий статус черновика
     * @return экземпляр исключения
     */
    public static AcceptanceInvalidStatusException notPending(String barcode, DraftStatus currentStatus) {
        return new AcceptanceInvalidStatusException(
                "Acceptance is allowed only for PENDING draft. Barcode: " + barcode + ", current status: " + currentStatus);
    }
}
