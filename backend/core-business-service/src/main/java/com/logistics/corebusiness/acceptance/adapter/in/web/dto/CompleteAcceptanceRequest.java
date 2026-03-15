package com.logistics.corebusiness.acceptance.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Запрос на завершение приёмки посылки.
 *
 * <h2>Назначение</h2>
 * Передаёт идентификатор черновика и ПВЗ, на котором создаётся итоговая накладная.
 */
@Builder
public record CompleteAcceptanceRequest(
        @NotNull(message = "ID черновика обязателен")
        Long draftId,

        @NotNull(message = "ID пункта приёмки обязателен")
        Long facilityId
) {
}
