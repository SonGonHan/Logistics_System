package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint получения накладной.
 */
@Operation(
        summary = "Получить накладную по ID",
        description = "Возвращает подтвержденную накладную по её ID",
        tags = {"Накладные"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Накладная успешно получена",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Накладная не найдена",
                content = @Content(mediaType = "application/json"))
})
public @interface GetWaybillOperation {
}
