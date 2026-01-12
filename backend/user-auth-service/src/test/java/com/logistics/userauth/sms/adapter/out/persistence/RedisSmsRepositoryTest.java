package com.logistics.userauth.sms.adapter.out.persistence;

import com.logistics.shared.redis.service.RedisService;
import com.logistics.userauth.sms.domain.SmsVerificationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisSmsRepository - тестирование Redis репозитория для SMS")
class RedisSmsRepositoryTest {

    @Mock
    private RedisService redisService;

    private RedisSmsRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RedisSmsRepository(redisService);
        ReflectionTestUtils.setField(repository, "defaultTtlMinutes", 5L);
    }

    @Test
    @DisplayName("Должен сохранить код верификации в Redis")
    void shouldSaveVerificationCode() {
        // Given
        String phone = "79991234567";
        var code = SmsVerificationCode.builder()
                .phone(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();
        long ttl = 5L;

        // When
        repository.save(code, ttl);

        // Then
        verify(redisService).set(
                eq("sms:verification:" + phone),
                eq(code),
                eq(Duration.ofMinutes(ttl))
        );
    }

    @Test
    @DisplayName("Должен найти код верификации по телефону")
    void shouldFindVerificationCodeByPhone() {
        // Given
        String phone = "79991234567";
        var expectedCode = SmsVerificationCode.builder()
                .phone(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();

        when(redisService.get(eq("sms:verification:" + phone), eq(SmsVerificationCode.class)))
                .thenReturn(Optional.of(expectedCode));

        // When
        Optional<SmsVerificationCode> result = repository.findByPhone(phone);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo(phone);
        assertThat(result.get().getCode()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional если код не найден")
    void shouldReturnEmptyOptionalIfCodeNotFound() {
        // Given
        String phone = "79991234567";
        when(redisService.get(anyString(), eq(SmsVerificationCode.class)))
                .thenReturn(Optional.empty());

        // When
        Optional<SmsVerificationCode> result = repository.findByPhone(phone);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Должен удалить код верификации")
    void shouldDeleteVerificationCode() {
        // Given
        String phone = "79991234567";

        // When
        repository.deleteVerificationCode(phone);

        // Then
        verify(redisService).delete("sms:verification:" + phone);
    }

    @Test
    @DisplayName("Должен инкрементировать попытки верификации")
    void shouldIncrementAttempts() {
        // Given
        String phone = "79991234567";
        var code = SmsVerificationCode.builder()
                .phone(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(1)
                .build();

        when(redisService.get(eq("sms:verification:" + phone), eq(SmsVerificationCode.class)))
                .thenReturn(Optional.of(code));
        when(redisService.getTtl(eq("sms:verification:" + phone), eq(TimeUnit.MINUTES)))
                .thenReturn(Optional.of(4L));

        // When
        repository.incrementAttempts(phone);

        // Then
        verify(redisService).set(
                eq("sms:verification:" + phone),
                argThat(c -> ((SmsVerificationCode) c).getAttempts() == 2),
                eq(Duration.ofMinutes(4L))
        );
    }

    @Test
    @DisplayName("Должен пометить телефон как верифицированный")
    void shouldMarkPhoneAsVerified() {
        // Given
        String phone = "79991234567";
        long ttl = 10L;

        // When
        repository.markPhoneAsVerified(phone, ttl);

        // Then
        verify(redisService).set(
                eq("sms:verified:" + phone),
                eq("verified"),
                eq(Duration.ofMinutes(ttl))
        );
    }

    @Test
    @DisplayName("Должен проверить что телефон верифицирован")
    void shouldCheckIfPhoneIsVerified() {
        // Given
        String phone = "79991234567";
        when(redisService.get(eq("sms:verified:" + phone), eq(String.class)))
                .thenReturn(Optional.of("verified"));

        // When
        boolean result = repository.isPhoneVerified(phone);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть false если телефон не верифицирован")
    void shouldReturnFalseIfPhoneNotVerified() {
        // Given
        String phone = "79991234567";
        when(redisService.get(eq("sms:verified:" + phone), eq(String.class)))
                .thenReturn(Optional.empty());

        // When
        boolean result = repository.isPhoneVerified(phone);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Должен удалить статус верификации")
    void shouldDeleteVerificationStatus() {
        // Given
        String phone = "79991234567";

        // When
        repository.deleteVerificationStatus(phone);

        // Then
        verify(redisService).delete("sms:verified:" + phone);
    }
}
