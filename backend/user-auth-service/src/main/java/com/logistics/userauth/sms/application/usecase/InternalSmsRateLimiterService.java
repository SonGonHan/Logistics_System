package com.logistics.userauth.sms.application.usecase;

import com.logistics.shared.redis.service.RateLimiterService;
import com.logistics.userauth.sms.application.exception.RateLimitExceededException;
import com.logistics.userauth.sms.application.port.in.InternalSmsRateLimiterUseCase;
import com.logistics.userauth.sms.application.port.in.command.InternalSmsRateLimiterCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalSmsRateLimiterService implements InternalSmsRateLimiterUseCase {

    private final RateLimiterService rateLimiterService;

    @Value("${app.sms.verification.resend-cooldown-seconds}")
    private long resendCooldownSeconds;

    @Value("${app.sms.verification.max-attempts}")
    private int maxAttempts;

    /**
     * Проверяет rate limit для отправки SMS на указанный номер.
     *
     * <p>Использует {@link RateLimiterService} для ограничения частоты запросов.
     *
     * @param command Команда для проверки перегрузки (phone)
     * @throws RateLimitExceededException если лимит превышен
     */
    @Override
    public void checkRateLimiter(InternalSmsRateLimiterCommand command) {

        String identifier = "sms:send:" + command.phone();

        if (rateLimiterService.isRateLimited(identifier, maxAttempts, Duration.ofSeconds(resendCooldownSeconds))) {
            log.warn("Rate limit exceeded for phone: {}", command.phone());
            throw new RateLimitExceededException("Повторная отправка возможна через 60 секунд");
        }
    }
}
