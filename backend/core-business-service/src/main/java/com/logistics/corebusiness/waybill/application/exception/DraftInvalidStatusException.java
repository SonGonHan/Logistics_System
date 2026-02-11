package com.logistics.corebusiness.waybill.application.exception;

import com.logistics.corebusiness.waybill.domain.DraftStatus;

/**
 * Исключение, выбрасываемое когда операция недопустима для текущего статуса черновика.
 *
 * <h2>Сценарии</h2>
 * - Попытка удалить CONFIRMED/CANCELLED черновик
 * - Попытка изменить CONFIRMED черновик
 * - Попытка повторно подтвердить CONFIRMED черновик
 *
 * <h2>Бизнес-правила</h2>
 * Жизненный цикл: PENDING → CONFIRMED/CANCELLED (финальные состояния)
 *
 * <h2>HTTP mapping</h2>
 * Должно быть обработано в @RestControllerAdvice и преобразовано в HTTP 409 Conflict.
 */
public class DraftInvalidStatusException extends RuntimeException {

    public DraftInvalidStatusException(String message) {
        super(message);
    }

    public static DraftInvalidStatusException forOperation(String operation, DraftStatus currentStatus) {
        return new DraftInvalidStatusException(
                "Cannot " + operation + " draft with status: " + currentStatus
        );
    }
}
