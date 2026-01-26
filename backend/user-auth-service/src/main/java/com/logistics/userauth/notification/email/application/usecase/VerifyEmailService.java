package com.logistics.userauth.notification.email.application.usecase;

import com.logistics.userauth.notification.common.application.usecase.AbstractVerifyService;
import com.logistics.userauth.notification.email.application.port.in.VerifyEmailUseCase;
import com.logistics.userauth.notification.email.application.port.in.command.VerifyEmailCommand;
import com.logistics.userauth.notification.email.application.port.out.EmailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис для верификации email через коды подтверждения.
 *
 * <p>Использует общую логику из {@link AbstractVerifyService}.</p>
 */
@Service
public class VerifyEmailService extends AbstractVerifyService implements VerifyEmailUseCase {

    @Value("${app.email.verification.max-attempts}")
    private int maxAttempts;

    @Value("${app.email.verification.verified-status-ttl-minutes:10}")
    private long verifiedStatusTtlMinutes;

    public VerifyEmailService(EmailRepository repository) {
        super(repository);
    }

    @Override
    protected String normalizeId(String id) {
        return id != null ? id.toLowerCase().trim() : id;
    }

    @Override
    protected int getMaxAttempts() {
        return maxAttempts;
    }

    @Override
    protected long getVerifiedStatusTtlMinutes() {
        return verifiedStatusTtlMinutes;
    }

    @Override
    protected String getChannelName() {
        return "email";
    }

    @Override
    public void verify(VerifyEmailCommand command) throws Throwable {
        verifyCode(command.email(), command.code());
    }
}
