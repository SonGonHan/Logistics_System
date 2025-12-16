package com.logistics.userauth.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint обновления access токена.
 *
 * @Operation для Swagger
 * - summary: \"Обновление access токена\"
 * - description: \"Использует refresh token для выдачи нового access token и нового refresh token (token rotation)\"
 * - tags: {\"Аутентификация\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Обновление access токена",
        description = "Использует refresh token для выдачи нового access token и нового refresh token (token rotation)",
        tags = {"Аутентификация"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Новые токены успешно выданы"
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Refresh token невалиден, истек или был отозван"
        )
})
public @interface RefreshOperation {
}
