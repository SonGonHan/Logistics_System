package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import lombok.Builder;

/**
 * Запрос на создание подтвержденной накладной.
 *
 * <h2>Варианты использования</h2>
 * - Из черновика: передается только draftId
 * - Напрямую: передаются recipientUserId, recipientAddress и pricingRuleId
 */
@Builder
public record CreateWaybillRequest(
        Long draftId,
        Long recipientUserId,
        String recipientAddress,
        Long pricingRuleId
) {
}
