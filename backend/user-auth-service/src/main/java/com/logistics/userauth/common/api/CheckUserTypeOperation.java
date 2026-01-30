package com.logistics.userauth.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint проверки типа пользователя.
 *
 * @Operation для Swagger
 * - summary: "Проверка типа пользователя"
 * - description: "Определяет метод аутентификации для пользователя (OTP для клиентов, пароль для сотрудников)"
 * - tags: {"Аутентификация"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Проверка типа пользователя",
        description = "Определяет, является ли пользователь клиентом (passwordless через OTP) или сотрудником (вход по паролю). " +
                "Если пользователь не найден, он будет зарегистрирован как клиент при первом входе.",
        tags = {"Аутентификация"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Тип пользователя успешно определен",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации входных данных (пустой identifier)"
        )
})
public @interface CheckUserTypeOperation {
}