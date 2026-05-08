package com.logistics.corebusiness.rating.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * DTO запроса для создания оценки доставки.
 *
 * @param waybillId ID накладной
 * @param score оценка от 1 до 5
 * @param comment текстовый отзыв (опционально)
 */
@Builder
public record SubmitRatingRequest(
        @NotNull(message = "ID накладной обязателен")
        Long waybillId,

        @NotNull(message = "Оценка обязательна")
        @Min(value = 1, message = "Оценка должна быть от 1 до 5")
        @Max(value = 5, message = "Оценка должна быть от 1 до 5")
        Integer score,

        String comment
) {}
