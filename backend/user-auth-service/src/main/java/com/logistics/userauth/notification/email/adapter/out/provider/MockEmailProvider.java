package com.logistics.userauth.notification.email.adapter.out.provider;

import com.logistics.userauth.notification.email.application.port.out.SendEmailPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Mock реализация Email сервиса для локальной разработки.
 * Просто логирует код в консоль вместо реальной отправки email.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "mock", matchIfMissing = true)
public class MockEmailProvider implements SendEmailPort {

    @Override
    public boolean sendVerificationCode(String email, String code) {
        log.info("╔════════════════════════════════════════════╗");
        log.info("║         MOCK EMAIL SERVICE                 ║");
        log.info("║  Email: {:<30} ║", email);
        log.info("║  Code:  {} ║", code);
        log.info("╚════════════════════════════════════════════╝");
        return true;
    }
}