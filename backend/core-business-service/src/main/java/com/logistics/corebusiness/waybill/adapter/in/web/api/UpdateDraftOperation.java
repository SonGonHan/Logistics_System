package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint обновления черновика накладной.
 *
 * @Operation для Swagger
 * - summary: "Обновить черновик накладной"
 * - description: "Обновляет данные черновика. Можно изменять только черновики в статусе PENDING. Цена пересчитывается автоматически при изменении веса или габаритов."
 * - tags: {"Черновики накладных"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Обновить черновик накладной",
        description = "Обновляет данные черновика. Можно изменять только черновики в статусе PENDING. Цена пересчитывается автоматически при изменении веса или габаритов.",
        tags = {"Черновики накладных"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Черновик успешно обновлен",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Черновик не найден",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Доступ запрещен (не ваш черновик)",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Невозможно изменить черновик в текущем статусе",
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
public @interface UpdateDraftOperation {
}
