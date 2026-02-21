package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import lombok.Builder;

/**
 * Запрос на обновление черновика накладной.
 *
 * <h2>Валидация</h2>
 * Все поля опциональны — обновляются только переданные.
 * Null значения означают "не изменять".
 *
 * <h2>Пример запроса</h2>
 * {
 *   "recipientAddress": "г. Санкт-Петербург, Невский пр., д. 1",
 *   "pricingRuleId": 5
 * }
 */
@Builder
public record UpdateDraftRequest(
        Long recipientUserId,

        String recipientAddress,

        Long pricingRuleId
) {
}