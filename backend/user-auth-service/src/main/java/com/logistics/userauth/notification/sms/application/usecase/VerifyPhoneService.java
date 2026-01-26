package com.logistics.userauth.notification.sms.application.usecase;

import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.notification.common.application.usecase.AbstractVerifyService;
import com.logistics.userauth.notification.sms.application.port.in.VerifyPhoneUseCase;
import com.logistics.userauth.notification.sms.application.port.in.command.VerifyPhoneCommand;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис для верификации телефонов через SMS коды.
 *
 * <p>Использует общую логику из {@link AbstractVerifyService}.</p>
 */
@Service
public class VerifyPhoneService extends AbstractVerifyService<SmsRepository> implements VerifyPhoneUseCase {

    @Value("${app.sms.verification.max-attempts}")
    private int maxAttempts;

    @Value("${app.sms.verification.verified-status-ttl-minutes:10}")
    private long verifiedStatusTtlMinutes;

    public VerifyPhoneService(SmsRepository repository) {
        super(repository);
    }

    @Override
    protected String normalizeId(String id) {
        return PhoneUtils.normalize(id);
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
        return "phone";
    }

    @Override
    public void verify(VerifyPhoneCommand command) throws Throwable {
        verifyCode(command.phone(), command.code());
    }
}
