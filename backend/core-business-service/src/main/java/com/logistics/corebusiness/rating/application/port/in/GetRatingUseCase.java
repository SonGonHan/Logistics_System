package com.logistics.corebusiness.rating.application.port.in;

import com.logistics.corebusiness.rating.adapter.in.web.dto.RatingResponse;
import com.logistics.corebusiness.rating.application.port.in.command.GetRatingCommand;

/**
 * Порт (интерфейс) для получения оценки по накладной.
 *
 * <h2>Назначение</h2>
 * Определяет контракт получения рейтинга для указанной накладной.
 *
 * <h2>Реализации</h2>
 * - {@link com.logistics.corebusiness.rating.application.usecase.GetRatingService}
 *
 * @see com.logistics.corebusiness.rating.domain.Rating
 * @see GetRatingCommand
 */
public interface GetRatingUseCase {

    /**
     * Получить оценку по накладной.
     *
     * @param command параметры запроса
     * @return рейтинг для указанной накладной
     */
    RatingResponse get(GetRatingCommand command);
}
