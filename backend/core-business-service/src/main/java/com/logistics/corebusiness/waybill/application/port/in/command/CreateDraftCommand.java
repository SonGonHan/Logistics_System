package com.logistics.corebusiness.waybill.application.port.in.command;

import lombok.Builder;

/**
 * Команда на создание черновика накладной.
 *
 * <h2>Обогащение данных</h2>
 * - draftCreatorId: Извлекается из Spring Security Authentication
 * - senderUserId: По умолчанию равен draftCreatorId (клиент отправляет сам себе)
 * - barcode: Генерируется в Service
 * - estimatedPrice: Рассчитывается как basePrice выбранного тарифного плана
 */
@Builder
public record CreateDraftCommand(
        Long draftCreatorId,
        Long senderUserId,
        String recipientPhone,
        String recipientAddress,
        Long pricingRuleId
) {
}