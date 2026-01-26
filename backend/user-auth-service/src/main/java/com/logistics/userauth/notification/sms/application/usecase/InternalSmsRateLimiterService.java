package com.logistics.userauth.notification.sms.application.usecase;

import com.logistics.shared.redis.service.RateLimiterService;
import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.notification.common.application.usecase.AbstractRateLimiterService;
import com.logistics.userauth.notification.sms.application.port.in.InternalSmsRateLimiterUseCase;
import com.logistics.userauth.notification.sms.application.port.in.command.InternalSmsRateLimiterCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис для проверки rate limit на отправку SMS.
 *
 * <p>Использует общую логику из {@link AbstractRateLimiterService}.</p>
 */
@Service
public class InternalSmsRateLimiterService extends AbstractRateLimiterService implements InternalSmsRateLimiterUseCase {

    @Value("${app.sms.verification.resend-cooldown-seconds}")
    private long resendCooldownSeconds;

    @Value("${app.sms.verification.max-attempts}")
    private int maxAttempts;

    public InternalSmsRateLimiterService(RateLimiterService rateLimiterService) {
        super(rateLimiterService);
    }

    @Override
    protected String getIdentifierPrefix() {
        return "sms:send:";
    }

    @Override
    protected String normalizeId(String id) {
        return PhoneUtils.normalize(id);
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
        return "SMS";
    }

    @Override
    public void checkRateLimiter(InternalSmsRateLimiterCommand command) {
        checkRateLimiter(command.phone());
    }
}
