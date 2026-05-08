package com.logistics.corebusiness.rating.adapter.in.web.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO ответа с данными рейтинга.
 *
 * @param id ID рейтинга
 * @param waybillId ID накладной
 * @param userId ID пользователя
 * @param score оценка (1-5)
 * @param comment текстовый отзыв
 * @param createdAt дата создания
 */
@Builder
public record RatingResponse(
        Long id,
        Long waybillId,
        Long userId,
        Integer score,
        String comment,
        LocalDateTime createdAt
) {}
