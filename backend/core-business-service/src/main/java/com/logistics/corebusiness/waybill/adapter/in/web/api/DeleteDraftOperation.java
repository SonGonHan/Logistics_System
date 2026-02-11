package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint удаления черновика накладной.
 *
 * @Operation для Swagger
 * - summary: "Удалить черновик накладной"
 * - description: "Удаляет черновик накладной. Можно удалить только черновики в статусе PENDING."
 * - tags: {"Черновики накладных"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Удалить черновик накладной",
        description = "Удаляет черновик накладной. Можно удалить только черновики в статусе PENDING.",
        tags = {"Черновики накладных"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "204",
                description = "Черновик успешно удален",
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
                description = "Невозможно удалить черновик в текущем статусе",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")
        )
})
public @interface DeleteDraftOperation {
}
