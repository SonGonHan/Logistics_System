package com.logistics.corebusiness.waybill.application.port.in.command;

import lombok.Builder;

/**
 * Команда на получение списка накладных отправителя.
 *
 * <h2>Назначение</h2>
 * Ограничивает выборку накладных идентификатором отправителя.
 */
@Builder
public record ListWaybillsCommand(
        Long senderUserId
) {
}
