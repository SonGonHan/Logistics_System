package com.logistics.userauth.notification.email.application.port.in;

import com.logistics.userauth.notification.common.application.exception.RateLimitExceededException;
import com.logistics.userauth.notification.email.application.port.in.command.InternalEmailRateLimiterCommand;

/**
 * Внутренний use case для проверки rate limit на отправку email.
 * Используется внутри SendVerificationCodeService для защиты от спама.
 */
public interface InternalEmailRateLimiterUseCase {

    /**
     * Проверяет, не превышен ли лимит на отправку email
     *
     * @param command команда с email для проверки
     * @throws RateLimitExceededException если лимит превышен
     */
    void checkRateLimiter(InternalEmailRateLimiterCommand command);
}
