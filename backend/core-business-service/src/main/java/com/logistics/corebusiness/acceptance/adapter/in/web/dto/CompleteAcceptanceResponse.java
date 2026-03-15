package com.logistics.corebusiness.acceptance.adapter.in.web.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ после завершения приёмки и создания накладной.
 *
 * <h2>Назначение</h2>
 * Возвращает идентификатор и номер созданной накладной, её итоговую цену и момент приёмки.
 */
@Builder
public record CompleteAcceptanceResponse(
        Long waybillId,
        String waybillNumber,
        BigDecimal finalPrice,
        String waybillStatus,
        LocalDateTime acceptedAt
) {
}
