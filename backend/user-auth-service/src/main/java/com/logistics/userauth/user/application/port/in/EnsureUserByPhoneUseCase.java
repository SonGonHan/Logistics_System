package com.logistics.userauth.user.application.port.in;

import com.logistics.userauth.user.application.port.in.command.EnsureUserByPhoneCommand;

/**
 * Use case: найти или создать пользователя по номеру телефона.
 *
 * <p>Возвращает ID существующего пользователя, либо создаёт нового CLIENT
 * и возвращает его ID.
 */
public interface EnsureUserByPhoneUseCase {
    Long ensure(EnsureUserByPhoneCommand command);
}