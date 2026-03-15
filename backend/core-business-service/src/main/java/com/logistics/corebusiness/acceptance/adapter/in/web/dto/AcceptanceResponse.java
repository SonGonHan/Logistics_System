package com.logistics.corebusiness.acceptance.adapter.in.web.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Ответ с данными черновика после начала приёмки.
 *
 * <h2>Назначение</h2>
 * Возвращает оператору ПВЗ ключевые сведения о найденном черновике для визуальной проверки.
 */
@Builder
public record AcceptanceResponse(
        Long draftId,
        String barcode,
        Long senderUserId,
        Long recipientUserId,
        String recipientAddress,
        BigDecimal estimatedPrice,
        String draftStatus
) {
}
