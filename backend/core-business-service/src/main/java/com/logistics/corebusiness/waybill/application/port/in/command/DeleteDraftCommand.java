package com.logistics.corebusiness.waybill.application.port.in.command;

import lombok.Builder;

/**
 * Команда на удаление черновика.
 *
 * <h2>Обогащение данных</h2>
 * - userId: Извлекается из Spring Security Authentication (для проверки владения)
 */
@Builder
public record DeleteDraftCommand(
        Long draftId,
        Long userId
) {
}
