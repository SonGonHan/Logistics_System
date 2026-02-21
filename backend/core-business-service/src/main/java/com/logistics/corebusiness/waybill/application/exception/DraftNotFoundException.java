package com.logistics.corebusiness.waybill.application.exception;

/**
 * Исключение, выбрасываемое когда черновик накладной не найден.
 *
 * <h2>Использование</h2>
 * - Поиск по ID: "Draft not found with id: 123"
 * - Поиск по barcode: "Draft not found with barcode: ABC123"
 *
 * <h2>HTTP mapping</h2>
 * Должно быть обработано в @RestControllerAdvice и преобразовано в HTTP 404 Not Found.
 */
public class DraftNotFoundException extends RuntimeException {

    public DraftNotFoundException(String message) {
        super(message);
    }

    public static DraftNotFoundException byId(Long id) {
        return new DraftNotFoundException("Draft not found with id: " + id);
    }

    public static DraftNotFoundException byBarcode(String barcode) {
        return new DraftNotFoundException("Draft not found with barcode: " + barcode);
    }
}
