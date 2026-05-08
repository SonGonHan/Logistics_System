package com.logistics.corebusiness.rating.application.usecase;

import com.logistics.corebusiness.rating.adapter.in.RatingControllerMapper;
import com.logistics.corebusiness.rating.adapter.in.web.dto.RatingResponse;
import com.logistics.corebusiness.rating.application.exception.RatingNotFoundException;
import com.logistics.corebusiness.rating.application.port.in.GetRatingUseCase;
import com.logistics.corebusiness.rating.application.port.in.command.GetRatingCommand;
import com.logistics.corebusiness.rating.application.port.out.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис получения оценки доставки по накладной.
 *
 * <h2>Бизнес-логика</h2>
 * Поиск рейтинга по waybillId. Выбрасывает 404 если не найден.
 *
 * @see GetRatingUseCase
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetRatingService implements GetRatingUseCase {

    private final RatingRepository ratingRepository;

    @Override
    public RatingResponse get(GetRatingCommand command) {
        var rating = ratingRepository.findByWaybillId(command.waybillId())
                .orElseThrow(() -> RatingNotFoundException.byWaybillId(command.waybillId()));

        return RatingControllerMapper.toResponse(rating);
    }
}
