package com.logistics.corebusiness.rating.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.rating.adapter.in.RatingControllerMapper;
import com.logistics.corebusiness.rating.adapter.in.web.dto.RatingResponse;
import com.logistics.corebusiness.rating.application.exception.RatingDuplicateException;
import com.logistics.corebusiness.rating.application.exception.RatingValidationException;
import com.logistics.corebusiness.rating.application.port.in.SubmitRatingUseCase;
import com.logistics.corebusiness.rating.application.port.in.command.SubmitRatingCommand;
import com.logistics.corebusiness.rating.application.port.out.RatingRepository;
import com.logistics.corebusiness.rating.domain.Rating;
import com.logistics.corebusiness.waybill.application.exception.WaybillNotFoundException;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Сервис создания оценки доставки.
 *
 * <h2>Бизнес-логика</h2>
 * <ol>
 *   <li>Найти накладную по waybillId -> 404 если не найдена</li>
 *   <li>Проверить статус == DELIVERED -> 400 если нет</li>
 *   <li>Проверить отсутствие дубликата -> 409 если рейтинг уже существует</li>
 *   <li>Создать и сохранить Rating</li>
 *   <li>Записать аудит RATING_SUBMIT</li>
 * </ol>
 *
 * @see SubmitRatingUseCase
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SubmitRatingService implements SubmitRatingUseCase {

    private final RatingRepository ratingRepository;
    private final WaybillRepository waybillRepository;
    private final CreateAuditLogUseCase auditLogUseCase;

    @Override
    public RatingResponse submit(SubmitRatingCommand command) {
        var waybill = waybillRepository.findById(command.waybillId())
                .orElseThrow(() -> WaybillNotFoundException.byId(command.waybillId()));

        if (waybill.getStatus() != WaybillStatus.DELIVERED) {
            throw RatingValidationException.waybillNotDelivered(
                    command.waybillId(), waybill.getStatus().name());
        }

        if (ratingRepository.existsByWaybillId(command.waybillId())) {
            throw RatingDuplicateException.forWaybill(command.waybillId());
        }

        var rating = Rating.builder()
                .waybillId(command.waybillId())
                .ratingCreatorId(command.userId())
                .score(command.score())
                .comment(command.comment())
                .createdAt(LocalDateTime.now())
                .build();

        var saved = ratingRepository.save(rating);

        auditLogUseCase.create(new CreateAuditLogCommand(
                command.userId(),
                "RATING_SUBMIT",
                null,
                null,
                Map.of(
                        "waybillId", command.waybillId(),
                        "score", command.score()
                ),
                "ratings_reviews",
                saved.getId()
        ));

        return RatingControllerMapper.toResponse(saved);
    }
}
