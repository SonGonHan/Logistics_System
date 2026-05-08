package com.logistics.corebusiness.rating.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для документирования endpoint отправки рейтинга.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Оставить оценку доставки",
        description = "Создает оценку (1-5) и отзыв для накладной со статусом DELIVERED. Допускается одна оценка на накладную.",
        tags = {"Рейтинг"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Оценка успешно создана",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации (некорректный score или накладная не DELIVERED)",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Накладная не найдена",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Оценка для данной накладной уже существует",
                content = @Content(mediaType = "application/json"))
})
public @interface SubmitRatingOperation {}
