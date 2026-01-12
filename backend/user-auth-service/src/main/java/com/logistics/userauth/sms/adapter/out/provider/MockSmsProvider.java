package com.logistics.userauth.sms.adapter.out.provider;

import com.logistics.userauth.sms.application.port.out.SendSmsPort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Mock реализация SMS сервиса для локальной разработки.
 * Просто логирует код в консоль вместо реальной отправки.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsProvider implements SendSmsPort {
    @Override
    public boolean sendVerificationCode(String phone, String code) {
        log.info("╔════════════════════════════════════════════╗");
        log.info("║         MOCK SMS SERVICE                   ║");
        log.info("║  Phone: {}                      ║", phone);
        log.info("║  Code:  {}                          ║", code);
        log.info("╚════════════════════════════════════════════╝");
        return true;
    }
}
