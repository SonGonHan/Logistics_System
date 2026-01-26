package com.logistics.userauth.notification.email.application.usecase;

import com.logistics.shared.redis.service.RateLimiterService;
import com.logistics.userauth.notification.common.application.usecase.AbstractRateLimiterService;
import com.logistics.userauth.notification.email.application.port.in.InternalEmailRateLimiterUseCase;
import com.logistics.userauth.notification.email.application.port.in.command.InternalEmailRateLimiterCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис для проверки rate limit на отправку email.
 *
 * <p>Использует общую логику из {@link AbstractRateLimiterService}.</p>
 */
@Service
public class InternalEmailRateLimiterService extends AbstractRateLimiterService implements InternalEmailRateLimiterUseCase {

    @Value("${app.email.verification.resend-cooldown-seconds}")
    private long resendCooldownSeconds;

    @Value("${app.email.verification.max-attempts}")
    private int maxAttempts;

    public InternalEmailRateLimiterService(RateLimiterService rateLimiterService) {
        super(rateLimiterService);
    }

    @Override
    protected String getIdentifierPrefix() {
        return "email:send:";
    }

    @Override
    protected String normalizeId(String id) {
        return id != null ? id.toLowerCase().trim() : id;
    }

    @Override
    protected long getResendCooldownSeconds() {
        return resendCooldownSeconds;
    }

    @Override
    protected int getMaxAttempts() {
        return maxAttempts;
    }

    @Override
    protected String getChannelName() {
        return "Email";
    }

    @Override
    public void checkRateLimiter(InternalEmailRateLimiterCommand command) {
        checkRateLimiter(command.email());
    }
}
