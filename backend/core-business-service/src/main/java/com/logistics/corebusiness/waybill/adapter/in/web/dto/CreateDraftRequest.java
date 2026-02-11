package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Запрос на создание черновика накладной.
 *
 * <h2>Валидация</h2>
 * - recipientUserId: ID получателя (обязательно)
 * - recipientAddress: Адрес доставки (обязательно)
 * - weightDeclared: Заявленный вес в кг (обязательно, > 0)
 * - dimensions: Габариты посылки (опционально)
 *
 * <h2>Пример запроса</h2>
 * {
 *   "recipientUserId": 123,
 *   "recipientAddress": "г. Москва, ул. Ленина, д. 10, кв. 5",
 *   "weightDeclared": 2.5,
 *   "dimensions": {
 *     "length": 30,
 *     "width": 20,
 *     "height": 15
 *   }
 * }
 */
@Builder
public record CreateDraftRequest(
        @NotNull(message = "ID получателя обязателен")
        Long recipientUserId,

        @NotBlank(message = "Адрес доставки обязателен")
        String recipientAddress,

        @NotNull(message = "Заявленный вес обязателен")
        @DecimalMin(value = "0.01", message = "Вес должен быть больше 0")
        BigDecimal weightDeclared,

        Long pricingRuleId,

        DimensionsDto dimensions
) {
}
