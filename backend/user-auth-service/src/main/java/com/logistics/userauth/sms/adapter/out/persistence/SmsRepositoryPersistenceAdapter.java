package com.logistics.userauth.sms.adapter.out.persistence;

import com.logistics.userauth.sms.application.port.out.SmsRepository;
import com.logistics.userauth.sms.domain.SmsVerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SmsRepositoryPersistenceAdapter implements SmsRepository {
    private final RedisSmsRepository smsRepository;

    @Override
    public void save(SmsVerificationCode code, long ttlMinutes) {
        smsRepository.save(code, ttlMinutes);
    }

    @Override
    public Optional<SmsVerificationCode> findByPhone(String phone) {
        return smsRepository.findByPhone(phone);
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
    public void markPhoneAsVerified(String phone, long ttlMinutes) {
        smsRepository.markPhoneAsVerified(phone, ttlMinutes);
    }

    @Override
    public boolean isPhoneVerified(String phone) {
        return smsRepository.isPhoneVerified(phone);
    }

    @Override
    public void deleteVerificationStatus(String phone) {
        smsRepository.deleteVerificationStatus(phone);
    }
}
