package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint получения черновика по штрих-коду.
 *
 * @Operation для Swagger
 * - summary: "Получить черновик по штрих-коду"
 * - description: "Возвращает информацию о черновике накладной по уникальному barcode. Пользователь может получить только свои черновики."
 * - tags: {"Черновики накладных"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Получить черновик по штрих-коду",
        description = "Возвращает информацию о черновике накладной по уникальному barcode. Пользователь может получить только свои черновики.",
        tags = {"Черновики накладных"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Черновик успешно получен",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Черновик с таким barcode не найден",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Доступ запрещен (не ваш черновик)",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")
        )
})
public @interface GetDraftByBarcodeOperation {
}
