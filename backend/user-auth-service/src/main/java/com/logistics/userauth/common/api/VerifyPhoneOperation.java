package com.logistics.userauth.common.api;

import com.logistics.userauth.notification.sms.adapter.in.web.dto.VerifyPhoneResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint проверки SMS кода.
 *
 * @Operation для Swagger
 * - summary: \"Проверить SMS код\"
 * - description: \"Проверяет введенный пользователем 6-значный код. Максимум 3 попытки ввода. После 3 неудачных попыток код удаляется.\"
 * - tags: {\"SMS\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Проверить SMS код",
        description = "Проверяет введенный пользователем 6-значный код. Максимум 3 попытки ввода. После 3 неудачных попыток код удаляется.",
        tags = {"SMS"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Телефон успешно подтвержден",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = VerifyPhoneResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Неверный код или истек срок действия",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Код не найден. Запросите новый код.",
                content = @Content(mediaType = "application/json")
        )
})
public @interface VerifyPhoneOperation {
}
