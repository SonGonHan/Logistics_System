package com.logistics.corebusiness.acceptance.application.port.in.command;

import lombok.Builder;

/**
 * Команда на начало приёмки посылки на ПВЗ.
 *
 * <h2>Назначение</h2>
 * Инкапсулирует входные данные, необходимые для поиска черновика по barcode и проверки ПВЗ.
 */
@Builder
public record StartAcceptanceCommand(
        Long operatorId,
        String barcode,
        Long facilityId
) {
}
