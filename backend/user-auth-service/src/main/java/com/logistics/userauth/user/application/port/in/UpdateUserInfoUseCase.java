package com.logistics.userauth.user.application.port.in;

import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.command.UpdateUserInfoCommand;

/**
 * Inbound port (use case) обновления информации о пользователе.
 *
 * <p>Определяет контракт application layer для изменения email/phone/FIO и (опционально) пароля.
 * Реализация отвечает за бизнес-проверки (например, подтверждение телефона перед сменой номера).
 */
public interface UpdateUserInfoUseCase {
    UserInfoResponse update(UpdateUserInfoCommand command);
}