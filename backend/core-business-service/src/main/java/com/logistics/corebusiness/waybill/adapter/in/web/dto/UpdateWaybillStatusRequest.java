package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Запрос на изменение статуса накладной.
 *
 * <h2>Валидация</h2>
 * newStatus обязателен, facilityId и notes передаются опционально.
 */
@Builder
public record UpdateWaybillStatusRequest(
        @NotNull(message = "Новый статус обязателен")
        WaybillStatus newStatus,
        Long facilityId,
        String notes
) {
}
