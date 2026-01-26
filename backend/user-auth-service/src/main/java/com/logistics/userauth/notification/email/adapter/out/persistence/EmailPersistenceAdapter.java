package com.logistics.userauth.notification.email.adapter.out.persistence;

import com.logistics.userauth.notification.email.application.port.out.EmailRepository;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Адаптер для persistence слоя email-верификации.
 * Реализует {@link EmailRepository} порт, делегируя вызовы в {@link RedisEmailRepository}.
 */
@Component
@RequiredArgsConstructor
public class EmailPersistenceAdapter implements EmailRepository {

    private final RedisEmailRepository emailRepository;

    @Override
    public void save(EmailVerificationCode code, long ttlMinutes) {
        emailRepository.save(code, ttlMinutes);
    }

    @Override
    public Optional<EmailVerificationCode> findById(String email) {
        return emailRepository.findById(email);
    }

    @Override
    public void deleteVerificationCode(String email) {
        emailRepository.deleteVerificationCode(email);
    }

    @Override
    public void incrementAttempts(String email) {
        emailRepository.incrementAttempts(email);
    }

    @Override
    public void markAsVerified(String email, long ttlMinutes) {
        emailRepository.markAsVerified(email, ttlMinutes);
    }

    @Override
    public boolean isVerified(String email) {
        return emailRepository.isVerified(email);
    }

    @Override
    public void deleteVerificationStatus(String email) {
        emailRepository.deleteVerificationStatus(email);
    }
}