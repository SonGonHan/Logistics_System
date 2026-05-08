package com.logistics.corebusiness.rating.application.port.in.command;

import lombok.Builder;

/**
 * Команда для получения оценки по накладной.
 *
 * @param waybillId ID накладной
 */
@Builder
public record GetRatingCommand(
        Long waybillId
) {}
