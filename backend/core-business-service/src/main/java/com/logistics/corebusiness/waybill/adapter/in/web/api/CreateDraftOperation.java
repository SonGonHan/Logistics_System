package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint создания черновика накладной.
 *
 * @Operation для Swagger
 * - summary: "Создать черновик накладной"
 * - description: "Создает новый черновик накладной с автоматической генерацией barcode и расчетом предварительной стоимости"
 * - tags: {"Черновики накладных"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Создать черновик накладной",
        description = "Создает новый черновик накладной с автоматической генерацией barcode и расчетом предварительной стоимости",
        tags = {"Черновики накладных"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Черновик успешно создан",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")
        )
})
public @interface CreateDraftOperation {
}
