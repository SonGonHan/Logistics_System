package com.logistics.corebusiness.acceptance.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint начала приёмки.
 */
@Operation(
        summary = "Начать приёмку посылки",
        description = "Начинает процесс приёмки посылки на ПВЗ по штрих-коду черновика",
        tags = {"Приёмка на ПВЗ"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Приёмка начата",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Черновик не найден",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Черновик уже подтверждён или отменён",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json"))
})
public @interface StartAcceptanceOperation {
}
