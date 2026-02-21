package com.logistics.corebusiness.waybill.application.port.in.command;

import lombok.Builder;

/**
 * Команда на получение черновика.
 *
 * <h2>Гибкий поиск</h2>
 * Поддерживает поиск либо по id, либо по barcode.
 * Ровно одно поле должно быть заполнено.
 *
 */
@Builder
public record GetDraftCommand(
        Long id,
        String barcode
) {
}
