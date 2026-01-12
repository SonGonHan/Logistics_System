package com.logistics.shared.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Удобный сервис для работы с Redis.
 * Предоставляет упрощенные методы для частых операций.
 */
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Сохранить значение с TTL.
     */
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * Получить значение.
     */
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(type.cast(value));
    }

    /**
     * Удалить ключ.
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * Проверить существование ключа.
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Получить оставшееся время жизни ключа.
     */
    public Optional<Long> getTtl(String key, TimeUnit unit) {
        Long ttl = redisTemplate.getExpire(key, unit);
        return ttl != null && ttl >= 0 ? Optional.of(ttl) : Optional.empty();
    }

    /**
     * Инкрементировать счетчик.
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * Инкрементировать с установкой TTL при первом создании.
     */
    public Long incrementWithTtl(String key, Duration ttl) {
        Long value = redisTemplate.opsForValue().increment(key);
        if (value != null && value == 1) {
            redisTemplate.expire(key, ttl);
        }
        return value;
    }
}
