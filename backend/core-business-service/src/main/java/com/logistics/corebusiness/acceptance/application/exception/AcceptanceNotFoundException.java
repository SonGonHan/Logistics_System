package com.logistics.corebusiness.acceptance.application.exception;

/**
 * Исключение, выбрасываемое когда сущность приёмки не найдена.
 *
 * <h2>Использование</h2>
 * - Поиск черновика по ID для завершения приёмки
 * - Поиск черновика по barcode для начала приёмки
 *
 * <h2>HTTP mapping</h2>
 * Должно быть обработано в {@code GlobalExceptionHandler} и преобразовано в HTTP 404 Not Found.
 */
public class AcceptanceNotFoundException extends RuntimeException {

    public AcceptanceNotFoundException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение для случая, когда черновик не найден по ID.
     *
     * @param id идентификатор черновика
     * @return экземпляр исключения
     */
    public static AcceptanceNotFoundException byId(Long id) {
        return new AcceptanceNotFoundException("Acceptance draft not found with id: " + id);
    }

    /**
     * Создаёт исключение для случая, когда черновик не найден по barcode.
     *
     * @param barcode штрих-код черновика
     * @return экземпляр исключения
     */
    public static AcceptanceNotFoundException byBarcode(String barcode) {
        return new AcceptanceNotFoundException("Acceptance draft not found with barcode: " + barcode);
    }
}
