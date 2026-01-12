package com.logistics.shared.redis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisService: тестирование операций с Redis")
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisService redisService;

    @BeforeEach
    void setUp() {
        redisService = new RedisService(redisTemplate);
    }

    @Test
    @DisplayName("Должен сохранить значение с TTL")
    void shouldSetValueWithTtl() {
        // Given
        String key = "test:key";
        String value = "test-value";
        Duration ttl = Duration.ofMinutes(5);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        redisService.set(key, value, ttl);

        // Then
        verify(valueOperations).set(key, value, ttl);
    }

    @Test
    @DisplayName("Должен получить значение по ключу")
    void shouldGetValueByKey() {
        // Given
        String key = "test:key";
        String expectedValue = "test-value";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(expectedValue);

        // When
        Optional<String> result = redisService.get(key, String.class);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedValue);
        verify(valueOperations).get(key);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional если значение не найдено")
    void shouldReturnEmptyOptionalWhenValueNotFound() {
        // Given
        String key = "nonexistent:key";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        // When
        Optional<String> result = redisService.get(key, String.class);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Должен удалить значение по ключу")
    void shouldDeleteValueByKey() {
        // Given
        String key = "test:key";
        when(redisTemplate.delete(key)).thenReturn(true);

        // When
        boolean result = redisService.delete(key);

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate).delete(key);
    }

    @Test
    @DisplayName("Должен проверить существование ключа")
    void shouldCheckIfKeyExists() {
        // Given
        String key = "test:key";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        // When
        boolean result = redisService.exists(key);

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate).hasKey(key);
    }

    @Test
    @DisplayName("Должен получить TTL ключа")
    void shouldGetTtl() {
        // Given
        String key = "test:key";
        when(redisTemplate.getExpire(key, TimeUnit.MINUTES)).thenReturn(5L);

        // When
        Optional<Long> result = redisService.getTtl(key, TimeUnit.MINUTES);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Должен инкрементировать значение")
    void shouldIncrementValue() {
        // Given
        String key = "counter:key";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(1L);

        // When
        Long result = redisService.increment(key);

        // Then
        assertThat(result).isEqualTo(1L);
        verify(valueOperations).increment(key);
    }

    @Test
    @DisplayName("Должен инкрементировать значение с TTL")
    void shouldIncrementValueWithTtl() {
        // Given
        String key = "counter:key";
        Duration ttl = Duration.ofMinutes(5);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(1L);
        when(redisTemplate.expire(key, ttl)).thenReturn(true);

        // When
        Long result = redisService.incrementWithTtl(key, ttl);

        // Then
        assertThat(result).isEqualTo(1L);
        verify(valueOperations).increment(key);
        verify(redisTemplate).expire(key, ttl);
    }

    @Test
    @DisplayName("Не должен устанавливать TTL при инкременте если значение null")
    void shouldNotSetTtlWhenIncrementReturnsNull() {
        // Given
        String key = "counter:key";
        Duration ttl = Duration.ofMinutes(5);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(null);

        // When
        Long result = redisService.incrementWithTtl(key, ttl);

        // Then
        assertThat(result).isNull();
        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }
}
