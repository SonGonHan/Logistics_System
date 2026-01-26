package com.logistics.userauth.notification.sms.adapter.out.persistence;

import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.notification.sms.domain.SmsVerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SmsPersistenceAdapter implements SmsRepository {
    private final RedisSmsRepository smsRepository;

    @Override
    public void save(SmsVerificationCode code, long ttlMinutes) {
        smsRepository.save(code, ttlMinutes);
    }

    @Override
    public Optional<SmsVerificationCode> findById(String phone) {
        return smsRepository.findById(phone);
    }

    @Override
    public void deleteVerificationCode(String phone) {
        smsRepository.deleteVerificationCode(phone);
    }

    @Override
    public void incrementAttempts(String phone) {
        smsRepository.incrementAttempts(phone);
    }

    @Override
    public void markAsVerified(String phone, long ttlMinutes) {
        smsRepository.markAsVerified(phone, ttlMinutes);
    }

    @Override
    public boolean isVerified(String phone) {
        return smsRepository.isVerified(phone);
    }

    @Override
    public void deleteVerificationStatus(String phone) {
        smsRepository.deleteVerificationStatus(phone);
    }
}
