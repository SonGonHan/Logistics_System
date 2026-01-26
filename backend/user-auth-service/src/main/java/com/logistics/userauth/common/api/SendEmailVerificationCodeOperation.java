package com.logistics.userauth.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint отправки Email кода.
 *
 * @Operation для Swagger
 * - summary: \"Отправить Email код верификации\"
 * - description: \"Генерирует и отправляет 6-значный код на указанный email адрес. Код действителен 5 минут. Максимум 3 попытки ввода.\"
 * - tags: {\"Email\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Отправить Email код верификации",
        description = "Генерирует и отправляет 6-значный код на указанный email адрес. Код действителен 5 минут. Максимум 3 попытки ввода.",
        tags = {"Email"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Email код успешно отправлен"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный формат email адреса",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "429",
                description = "Превышен лимит запросов. Повторите позже.",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "503",
                description = "Не удалось отправить Email. Попробуйте позже.",
                content = @Content(mediaType = "application/json")
        )
})
public @interface SendEmailVerificationCodeOperation {
}