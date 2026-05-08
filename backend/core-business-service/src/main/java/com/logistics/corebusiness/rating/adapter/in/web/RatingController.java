package com.logistics.corebusiness.rating.adapter.in.web;

import com.logistics.corebusiness.rating.adapter.in.web.api.GetRatingOperation;
import com.logistics.corebusiness.rating.adapter.in.web.api.SubmitRatingOperation;
import com.logistics.corebusiness.rating.adapter.in.web.dto.RatingResponse;
import com.logistics.corebusiness.rating.adapter.in.web.dto.SubmitRatingRequest;
import com.logistics.corebusiness.rating.application.port.in.GetRatingUseCase;
import com.logistics.corebusiness.rating.application.port.in.SubmitRatingUseCase;
import com.logistics.corebusiness.rating.application.port.in.command.GetRatingCommand;
import com.logistics.corebusiness.rating.application.port.in.command.SubmitRatingCommand;
import com.logistics.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для оценки доставки.
 *
 * <h2>Назначение</h2>
 * Предоставляет endpoints для создания и получения рейтингов.
 *
 * <h2>Архитектура</h2>
 * Thin-controller: не содержит бизнес-логики, формирует команды и делегирует
 * выполнение в application layer через Use Case интерфейсы.
 *
 * <h2>Security</h2>
 * ID пользователя извлекается из JWT токена через {@link SecurityUtils}.
 *
 * <h2>Endpoints</h2>
 * - POST /ratings/submit - оставить оценку
 * - GET /ratings/waybill/{waybillId} - получить оценку по накладной
 */
@Tag(name = "Рейтинг", description = "REST API endpoints для оценки доставки")
@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final SubmitRatingUseCase submitRatingUseCase;
    private final GetRatingUseCase getRatingUseCase;

    /**
     * Отправить оценку доставки.
     *
     * @param authentication аутентификация пользователя
     * @param request тело запроса
     * @return созданный рейтинг
     */
    @PostMapping("/submit")
    @SubmitRatingOperation
    public ResponseEntity<RatingResponse> submitRating(
            Authentication authentication,
            @Valid @RequestBody SubmitRatingRequest request
    ) {
        var userId = SecurityUtils.extractUserId(authentication);

        var command = SubmitRatingCommand.builder()
                .waybillId(request.waybillId())
                .userId(userId)
                .score(request.score())
                .comment(request.comment())
                .build();

        var response = submitRatingUseCase.submit(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить оценку по накладной.
     *
     * @param waybillId ID накладной
     * @return рейтинг для указанной накладной
     */
    @GetMapping("/waybill/{waybillId}")
    @GetRatingOperation
    public ResponseEntity<RatingResponse> getRatingByWaybill(@PathVariable Long waybillId) {
        var command = GetRatingCommand.builder()
                .waybillId(waybillId)
                .build();

        var response = getRatingUseCase.get(command);
        return ResponseEntity.ok(response);
    }
}
