package com.logistics.userauth.notification.email.application.port.in;

import com.logistics.userauth.notification.email.application.exception.EmailDeliveryException;
import com.logistics.userauth.notification.common.application.exception.RateLimitExceededException;
import com.logistics.userauth.notification.email.application.port.in.command.SendEmailVerificationCodeCommand;

/**
 * Use case для отправки кода верификации на email.
 * Генерирует код, сохраняет в Redis и отправляет на email пользователя.
 */
public interface SendEmailVerificationCodeUseCase {

    /**
     * Отправляет код верификации на указанный email
     *
     * @param command команда с данными для отправки кода
     * @throws RateLimitExceededException если превышен лимит запросов на отправку
     * @throws EmailDeliveryException если не удалось отправить email
     */
    void sendCode(SendEmailVerificationCodeCommand command);
}
