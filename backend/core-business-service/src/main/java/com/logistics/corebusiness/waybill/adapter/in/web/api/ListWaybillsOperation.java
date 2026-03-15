package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint списка накладных.
 */
@Operation(
        summary = "Получить список накладных пользователя",
        description = "Возвращает список подтвержденных накладных текущего отправителя",
        tags = {"Накладные"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список накладных успешно получен",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json"))
})
public @interface ListWaybillsOperation {
}
