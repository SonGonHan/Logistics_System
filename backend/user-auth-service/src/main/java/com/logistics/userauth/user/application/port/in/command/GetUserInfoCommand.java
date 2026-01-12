package com.logistics.userauth.user.application.port.in.command;

/**
 * Команда (command) для сценария получения профиля пользователя.
 *
 * <p>Используется как входной параметр use case {@link com.logistics.userauth.user.application.port.in.GetUserInfoUseCase}.
 *
 * @param userId идентификатор пользователя, профиль которого требуется получить.
 */
public record GetUserInfoCommand(
        Long userId
) {
}
