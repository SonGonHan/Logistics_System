package com.logistics.corebusiness.waybill.application.port.in.command;

import lombok.Builder;

/**
 * Команда на создание подтвержденной накладной.
 *
 * <h2>Назначение</h2>
 * Содержит параметры для создания накладной из черновика или напрямую.
 */
@Builder
public record CreateWaybillCommand(
        Long waybillCreatorId,
        Long draftId,
        Long senderUserId,
        Long recipientUserId,
        String recipientAddress,
        Long pricingRuleId
) {
}
