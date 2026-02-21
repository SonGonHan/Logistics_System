package com.logistics.corebusiness.waybill.adapter.in.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint получения списка черновиков пользователя.
 *
 * @Operation для Swagger
 * - summary: "Получить список черновиков пользователя"
 * - description: "Возвращает все черновики, созданные текущим пользователем. Поддерживает фильтрацию по статусу."
 * - tags: {"Черновики накладных"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Получить список черновиков пользователя",
        description = "Возвращает все черновики, созданные текущим пользователем. Поддерживает фильтрацию по статусу.",
        tags = {"Черновики накладных"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Список черновиков успешно получен",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")
        )
})
public @interface GetUserDraftListOperation {
}
