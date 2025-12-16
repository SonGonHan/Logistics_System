package com.logistics.userauth.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint авторизации.
 *
 * @Operation для Swagger
 * - summary: \"Авторизация пользователя\"
 * - description: \"Проверяет учетные данные пользователя (телефон/пароль) и выдает JWT access token и refresh token\"
 * - tags: {\"Аутентификация\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Авторизация пользователя",
        description = "Проверяет учетные данные пользователя (телефон/пароль) и выдает JWT access token и refresh token",
        tags = {"Аутентификация"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Успешная авторизация",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Неверные учетные данные (телефон или пароль)",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных"
        )
})
public @interface SignInOperation {
}
