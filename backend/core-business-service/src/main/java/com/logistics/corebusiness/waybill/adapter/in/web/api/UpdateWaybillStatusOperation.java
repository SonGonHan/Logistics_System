package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint изменения статуса накладной.
 */
@Operation(
        summary = "Обновить статус накладной",
        description = "Изменяет статус подтвержденной накладной по правилам допустимых переходов",
        tags = {"Накладные"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно обновлен",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Накладная не найдена",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Недопустимый переход статуса",
                content = @Content(mediaType = "application/json"))
})
public @interface UpdateWaybillStatusOperation {
}
