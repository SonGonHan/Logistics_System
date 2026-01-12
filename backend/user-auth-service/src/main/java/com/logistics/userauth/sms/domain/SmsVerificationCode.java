package com.logistics.userauth.sms.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Доменная модель для SMS кода верификации.
 */
@Data
@Builder
public class SmsVerificationCode {
    private String phone;
    private String code;
    private LocalDateTime expiresAt;
    private int attempts;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

}
