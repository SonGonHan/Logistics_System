package com.logistics.userauth.user.application.port.in;

import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.command.GetUserInfoCommand;

/**
 * Inbound port (use case) получения информации о пользователе.
 *
 * <p>Определяет контракт application layer для сценария чтения профиля пользователя.
 * Реализация должна получить пользователя из репозитория по {@code userId} и вернуть DTO ответа.
 */
public interface GetUserInfoUseCase {
    UserInfoResponse getUserInfo(GetUserInfoCommand command);
}
