package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import com.logistics.corebusiness.waybill.domain.DraftStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ с базовой информацией о черновике (для клиентов).
 *
 * <h2>Использование</h2>
 * Возвращается клиентам при получении/обновлении своих черновиков.
 * Содержит только необходимую информацию без служебных данных.
 *
 * <h2>Отличие от DetailedDraftResponse</h2>
 * Не содержит: senderUserId, draftCreatorId, pricingRuleId
 */
@Builder
public record DraftResponse(
        Long id,
        String barcode,
        Long recipientUserId,
        String recipientAddress,
        BigDecimal weightDeclared,
        DimensionsDto dimensions,
        BigDecimal estimatedPrice,
        DraftStatus draftStatus,
        LocalDateTime createdAt
) {
}
