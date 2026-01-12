package com.logistics.userauth.common.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint отправки SMS кода.
 *
 * @Operation для Swagger
 * - summary: \"Отправить SMS код верификации\"
 * - description: \"Генерирует и отправляет 6-значный код на указанный номер телефона. Код действителен 5 минут. Максимум 3 попытки ввода.\"
 * - tags: {\"SMS\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Отправить SMS код верификации",
        description = "Генерирует и отправляет 6-значный код на указанный номер телефона. Код действителен 5 минут. Максимум 3 попытки ввода.",
        tags = {"SMS"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "SMS код успешно отправлен"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный формат номера телефона",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "429",
                description = "Превышен лимит запросов. Повторите через 60 секунд.",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Не удалось отправить SMS. Попробуйте позже.",
                content = @Content(mediaType = "application/json")
        )
})
public @interface SendVerificationCodeOperation {
}
