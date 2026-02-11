package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Запрос на обновление черновика накладной.
 *
 * <h2>Валидация</h2>
 * Все поля опциональны - обновляются только переданные поля.
 * Null значения означают "не изменять".
 *
 * <h2>Пример запроса</h2>
 * {
 *   "recipientAddress": "г. Санкт-Петербург, Невский пр., д. 1",
 *   "weightDeclared": 3.0
 * }
 */
@Builder
public record UpdateDraftRequest(
        Long recipientUserId,

        String recipientAddress,

        @DecimalMin(value = "0.01", message = "Вес должен быть больше 0")
        BigDecimal weightDeclared,

        DimensionsDto dimensions,

        Long pricingRuleId
) {
}
