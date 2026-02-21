package com.logistics.corebusiness.waybill.application.exception;

/**
 * Исключение, выбрасываемое когда пользователь пытается получить доступ к чужому черновику.
 *
 * <h2>Сценарии</h2>
 * - Попытка прочитать чужой черновик
 * - Попытка изменить чужой черновик
 * - Попытка удалить чужой черновик
 *
 * <h2>Безопасность</h2>
 * Принцип Least Privilege: пользователь может работать только со своими черновиками.
 *
 * <h2>HTTP mapping</h2>
 * Должно быть обработано в @RestControllerAdvice и преобразовано в HTTP 403 Forbidden.
 */
public class DraftAccessDeniedException extends RuntimeException {

    public DraftAccessDeniedException(String message) {
        super(message);
    }

    public static DraftAccessDeniedException forOperation(String operation) {
        return new DraftAccessDeniedException(
                "Access denied: you can only " + operation + " your own drafts"
        );
    }
}
