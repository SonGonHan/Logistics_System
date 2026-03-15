package com.logistics.corebusiness.waybill.application.exception;

import com.logistics.corebusiness.waybill.domain.WaybillStatus;

/**
 * Исключение при недопустимом переходе статуса накладной.
 *
 * <h2>HTTP mapping</h2>
 * Обрабатывается в GlobalExceptionHandler -> HTTP 409 Conflict.
 */
public class WaybillInvalidStatusTransitionException extends RuntimeException {

    public WaybillInvalidStatusTransitionException(String message) {
        super(message);
    }

    public static WaybillInvalidStatusTransitionException invalidTransition(
            WaybillStatus from, WaybillStatus to) {
        return new WaybillInvalidStatusTransitionException(
                "Invalid status transition from " + from + " to " + to);
    }

    public static WaybillInvalidStatusTransitionException terminalStatus(WaybillStatus current) {
        return new WaybillInvalidStatusTransitionException(
                "Waybill is in terminal status: " + current + ". No further transitions allowed.");
    }
}
