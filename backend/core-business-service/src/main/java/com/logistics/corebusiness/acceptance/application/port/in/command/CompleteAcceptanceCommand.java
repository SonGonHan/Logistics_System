package com.logistics.corebusiness.acceptance.application.port.in.command;

import lombok.Builder;

/**
 * Команда на завершение приёмки посылки на ПВЗ.
 *
 * <h2>Назначение</h2>
 * Содержит данные, необходимые для создания накладной из черновика и фиксации точки приёмки.
 */
@Builder
public record CompleteAcceptanceCommand(
        Long operatorId,
        Long draftId,
        Long facilityId
) {
}
