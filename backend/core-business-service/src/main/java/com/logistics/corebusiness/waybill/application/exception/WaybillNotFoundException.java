package com.logistics.corebusiness.waybill.application.exception;

/**
 * Исключение, выбрасываемое когда накладная не найдена.
 *
 * <h2>Использование</h2>
 * - Поиск по ID: "Waybill not found with id: 123"
 * - Поиск по номеру: "Waybill not found with number: WB-260314-123456"
 *
 * <h2>HTTP mapping</h2>
 * Обрабатывается в GlobalExceptionHandler -> HTTP 404 Not Found.
 */
public class WaybillNotFoundException extends RuntimeException {

    public WaybillNotFoundException(String message) {
        super(message);
    }

    public static WaybillNotFoundException byId(Long id) {
        return new WaybillNotFoundException("Waybill not found with id: " + id);
    }

    public static WaybillNotFoundException byNumber(String number) {
        return new WaybillNotFoundException("Waybill not found with number: " + number);
    }
}
