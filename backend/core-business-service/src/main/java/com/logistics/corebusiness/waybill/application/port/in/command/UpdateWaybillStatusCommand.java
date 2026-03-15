package com.logistics.corebusiness.waybill.application.port.in.command;

import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import lombok.Builder;

/**
 * Команда на изменение статуса накладной.
 *
 * <h2>Назначение</h2>
 * Содержит новый статус и контекст выполнения операции.
 */
@Builder
public record UpdateWaybillStatusCommand(
        Long waybillId,
        WaybillStatus newStatus,
        Long operatorId,
        Long facilityId,
        String notes
) {
}
