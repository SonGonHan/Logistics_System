package com.logistics.corebusiness.rating.application.port.in;

import com.logistics.corebusiness.rating.adapter.in.web.dto.RatingResponse;
import com.logistics.corebusiness.rating.application.port.in.command.SubmitRatingCommand;

/**
 * Порт (интерфейс) для отправки оценки доставки.
 *
 * <h2>Назначение</h2>
 * Определяет контракт создания рейтинга для доставленной накладной.
 *
 * <h2>Бизнес-правила</h2>
 * <ul>
 *   <li>Накладная должна иметь статус DELIVERED</li>
 *   <li>Один пользователь может оставить только одну оценку на накладную</li>
 *   <li>Оценка от 1 до 5</li>
 * </ul>
 *
 * <h2>Реализации</h2>
 * - {@link com.logistics.corebusiness.rating.application.usecase.SubmitRatingService}
 *
 * @see com.logistics.corebusiness.rating.domain.Rating
 * @see SubmitRatingCommand
 */
public interface SubmitRatingUseCase {

    /**
     * Отправить оценку доставки.
     *
     * @param command параметры оценки
     * @return созданный рейтинг
     */
    RatingResponse submit(SubmitRatingCommand command);
}
