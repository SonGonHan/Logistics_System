package com.logistics.userauth.common.api;

import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Аннотация для документирования endpoint обновления информации о текущем пользователе.
 *
 * @Operation для Swagger
 * - summary: \"Обновить профиль текущего пользователя\"
 * - description: \"Обновляет email/phone/FIO, а при передаче паролей — выполняет смену пароля.\"
 * - tags: {\"Пользователи\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(
        summary = "Обновить профиль текущего пользователя",
        description = "Обновляет email/phone/FIO, а при передаче паролей — выполняет смену пароля.",
        tags = {"Users"}
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Профиль успешно обновлён",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserInfoResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Ошибка валидации запроса / старый пароль не передан при попытке смены пароля",
                content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Пользователь не аутентифицирован",
                content = @Content(mediaType = "application/json")
        )
})
public @interface UpdateInfoOperation {
}
