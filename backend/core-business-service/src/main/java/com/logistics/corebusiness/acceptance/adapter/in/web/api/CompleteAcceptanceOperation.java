package com.logistics.corebusiness.acceptance.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint завершения приёмки.
 */
@Operation(
        summary = "Завершить приёмку посылки",
        description = "Завершает приёмку на ПВЗ и создаёт накладную из черновика",
        tags = {"Приёмка на ПВЗ"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Приёмка завершена, накладная создана",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Черновик не найден",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Черновик уже подтверждён или отменён",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса или ПВЗ",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json"))
})
public @interface CompleteAcceptanceOperation {
}
