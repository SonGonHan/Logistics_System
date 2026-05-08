package com.logistics.corebusiness.rating.application.port.in.command;

import lombok.Builder;

/**
 * Команда для создания оценки доставки.
 *
 * @param waybillId ID накладной для оценки
 * @param userId ID пользователя, оставляющего оценку (из JWT)
 * @param score оценка от 1 до 5
 * @param comment текстовый отзыв (опционально)
 */
@Builder
public record SubmitRatingCommand(
        Long waybillId,
        Long userId,
        Integer score,
        String comment
) {}
