package com.logistics.userauth.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint регистрации.
 *
 * @Operation для Swagger
 * - summary: \"Регистрация нового пользователя\"
 * - description: \"Использует refresh token для выдачи нового access token и нового refresh token (token rotation)\"
 * - tags: {\"Аутентификация\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Регистрация нового пользователя",
        description = "Создает новый аккаунт пользователя с ролью CLIENT и выдает JWT tokens",
        tags = {"Аутентификация"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Пользователь успешно зарегистрирован"
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Пользователь с таким телефоном или email уже существует"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных"
        )
})
public @interface SignUpOperation {
}
