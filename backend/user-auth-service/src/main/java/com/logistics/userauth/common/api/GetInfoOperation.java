package com.logistics.userauth.common.api;

import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint получения информации о текущем пользователе.
 *
 * @Operation для Swagger
 * - summary: \"Получить информацию о текущем пользователе\"
 * - description: \"Возвращает профиль пользователя по JWT access token.\"
 * - tags: {\"Пользователи\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Получить информацию о текущем пользователе",
        description = "Возвращает профиль пользователя по JWT access token.",
        tags = {"Пользователи"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Профиль пользователя успешно получен",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserInfoResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Пользователь не аутентифицирован (отсутствует/невалиден JWT)",
                content = @Content(mediaType = "application/json")
        )
})
public @interface GetInfoOperation {
}
