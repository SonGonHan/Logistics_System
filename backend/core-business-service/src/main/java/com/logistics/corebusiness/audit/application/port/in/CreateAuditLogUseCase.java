package com.logistics.corebusiness.audit.application.port.in;

import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;

/**
 * Use Case для создания записи в журнале аудита.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для логирования действий в core-business-service:
 * - Черновики (DRAFT_CREATE, DRAFT_UPDATE, DRAFT_CANCEL)
 * - Приёмка на ПВЗ (PVZ_ACCEPTANCE_START, PVZ_WEIGHT_VERIFY, PVZ_PHOTO_UPLOAD)
 * - Накладные (WAYBILL_FINALIZE, WAYBILL_CREATE, WAYBILL_STATUS_CHANGE, WAYBILL_CANCEL)
 * - Дополнительные услуги (WAYBILL_SERVICE_ADD)
 * - Рейтинг (RATING_SUBMIT)
 *
 * <h2>Имплементация</h2>
 * @see com.logistics.corebusiness.audit.application.usecase.CreateAuditLogService
 */
public interface CreateAuditLogUseCase {
    void create(CreateAuditLogCommand command);
}
