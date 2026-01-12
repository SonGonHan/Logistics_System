package com.logistics.shared.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Дополнительные настройки Redis для Logistics System.
 */
@Data
@Component
@ConfigurationProperties(prefix = "logistics.redis")
public class RedisProperties {

    /**
     * Включить/выключить Redis для всего приложения.
     */
    private boolean enabled = true;

    /**
     * Префикс для всех ключей (например, "logistics:user-auth:")
     */
    private String keyPrefix = "logistics";

    /**
     * TTL по умолчанию для кэшей (в минутах)
     */
    private long defaultCacheTtlMinutes = 60;

    /**
     * Настройки для SMS кодов
     */
    private SmsCodeProperties smsCode = new SmsCodeProperties();

    @Data
    public static class SmsCodeProperties {
        private String keyPrefix = "sms:verification:";
        private long ttlMinutes = 5;
        private int maxAttempts = 3;
    }
}
