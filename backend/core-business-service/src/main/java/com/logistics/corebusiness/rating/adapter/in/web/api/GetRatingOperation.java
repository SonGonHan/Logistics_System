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
 * Аннотация для документирования endpoint получения рейтинга.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Получить оценку по накладной",
        description = "Возвращает оценку и отзыв для указанной накладной",
        tags = {"Рейтинг"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Оценка найдена",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Оценка не найдена для данной накладной",
                content = @Content(mediaType = "application/json"))
})
public @interface GetRatingOperation {}
