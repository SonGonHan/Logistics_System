package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import com.logistics.corebusiness.waybill.domain.DraftStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ с полной информацией о черновике (для работников).
 *
 * <h2>Использование</h2>
 * Возвращается работникам ПВЗ при обработке черновиков.
 * Содержит все данные, включая служебную информацию.
 *
 * <h2>Отличие от DraftResponse</h2>
 * Дополнительно содержит: senderUserId, draftCreatorId, pricingRuleId
 */
@Builder
public record DetailedDraftResponse(
        Long id,
        String barcode,
        Long draftCreatorId,
        Long senderUserId,
        Long recipientUserId,
        String recipientAddress,
        BigDecimal weightDeclared,
        DimensionsDto dimensions,
        Long pricingRuleId,
        BigDecimal estimatedPrice,
        DraftStatus draftStatus,
        LocalDateTime createdAt
) {
}
