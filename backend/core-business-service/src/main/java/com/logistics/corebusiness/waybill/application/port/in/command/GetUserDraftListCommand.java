package com.logistics.corebusiness.waybill.application.port.in.command;

import com.logistics.corebusiness.waybill.domain.DraftStatus;
import lombok.Builder;

/**
 * Команда на получение списка черновиков пользователя.
 *
 * <h2>Фильтрация</h2>
 * - status: Опциональная фильтрация по статусу (null = все статусы)
 *
 * <h2>Обогащение данных</h2>
 * - userId: Извлекается из Spring Security Authentication
 */
@Builder
public record GetUserDraftListCommand(
        Long userId,
        DraftStatus status
) {
}
