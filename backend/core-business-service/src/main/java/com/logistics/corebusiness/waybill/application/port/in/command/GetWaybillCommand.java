package com.logistics.corebusiness.waybill.application.port.in.command;

import lombok.Builder;

/**
 * Команда на получение накладной.
 *
 * <h2>Назначение</h2>
 * Позволяет искать накладную либо по идентификатору, либо по номеру.
 */
@Builder
public record GetWaybillCommand(
        Long id,
        String waybillNumber
) {
}
