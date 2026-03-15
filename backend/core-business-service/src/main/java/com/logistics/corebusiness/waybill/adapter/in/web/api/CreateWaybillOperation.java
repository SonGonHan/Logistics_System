package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint создания накладной.
 */
@Operation(
        summary = "Создать накладную",
        description = "Создает подтвержденную накладную из черновика (по draftId) или напрямую с указанием всех полей",
        tags = {"Накладные"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Накладная успешно создана",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Черновик не найден",
                content = @Content(mediaType = "application/json"))
})
public @interface CreateWaybillOperation {
}
