package com.logistics.corebusiness.waybill.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Запрос на создание черновика накладной.
 *
 * <h2>Валидация</h2>
 * - recipientPhone: Телефон получателя (обязательно). Если пользователя нет — создаётся автоматически.
 * - recipientAddress: Адрес доставки (обязательно)
 * - pricingRuleId: ID тарифного плана (обязательно). Тариф кодирует категорию веса и габаритов.
 *
 * <h2>Пример запроса</h2>
 * {
 *   "recipientPhone": "+79001234567",
 *   "recipientAddress": "г. Москва, ул. Ленина, д. 10, кв. 5",
 *   "pricingRuleId": 3
 * }
 */
@Builder
public record CreateDraftRequest(
        @NotBlank(message = "Телефон получателя обязателен")
        String recipientPhone,

        @NotBlank(message = "Адрес доставки обязателен")
        String recipientAddress,

        @NotNull(message = "Тарифный план обязателен")
        Long pricingRuleId
) {
}