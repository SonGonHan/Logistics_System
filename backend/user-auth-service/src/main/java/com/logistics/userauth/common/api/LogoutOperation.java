package com.logistics.userauth.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint выхода из аккаунта.
 *
 * @Operation для Swagger
 * - summary: \"Выход из системы\"
 * - description: \"Отзывает (revoke) refresh token, делая его неиспользуемым\"
 * - tags: {\"Аутентификация\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Выход из системы",
        description = "Отзывает (revoke) refresh token, делая его неиспользуемым",
        tags = {"Аутентификация"}
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Успешный выход"),
        @ApiResponse(responseCode = "401", description = "Refresh token невалиден")
})
public @interface LogoutOperation {
}
