package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO ответа с подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Передает клиенту полные данные накладной.
 */
@Builder
public record WaybillResponse(
        Long id,
        String waybillNumber,
        Long waybillCreatorId,
        Long senderUserId,
        Long recipientUserId,
        String recipientAddress,
        Long pricingRuleId,
        BigDecimal finalPrice,
        WaybillStatus status,
        LocalDateTime createdAt,
        LocalDateTime acceptedAt
) {
}
