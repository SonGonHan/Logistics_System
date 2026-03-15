package com.logistics.corebusiness.acceptance.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Запрос на начало приёмки посылки.
 *
 * <h2>Назначение</h2>
 * Передаёт штрих-код черновика и идентификатор ПВЗ, на котором оператор начинает приёмку.
 */
@Builder
public record StartAcceptanceRequest(
        @NotBlank(message = "Штрих-код обязателен")
        String barcode,

        @NotNull(message = "ID пункта приёмки обязателен")
        Long facilityId
) {
}
