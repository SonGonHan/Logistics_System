package com.logistics.userauth.notification.email.application.port.in;

import com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.notification.email.application.port.in.command.VerifyEmailCommand;

/**
 * Use case для верификации email с помощью кода.
 * Проверяет введённый код и отмечает email как верифицированный.
 */
public interface VerifyEmailUseCase {

    /**
     * Верифицирует email с помощью кода подтверждения
     *
     * @param command команда с email и кодом
     * @throws InvalidVerificationCodeException если код неверный, истёк или превышено количество попыток
     */
    void verify(VerifyEmailCommand command) throws Throwable;
}
