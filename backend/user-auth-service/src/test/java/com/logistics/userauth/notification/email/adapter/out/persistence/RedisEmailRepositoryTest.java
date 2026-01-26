package com.logistics.userauth.notification.email.adapter.out.persistence;

import com.logistics.shared.redis.service.RedisService;
import com.logistics.userauth.notification.email.adapter.out.persistence.RedisEmailRepository;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;
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
@DisplayName("RedisEmailRepository - тестирование Redis репозитория для Email")
class RedisEmailRepositoryTest {

    @Mock
    private RedisService redisService;

    private RedisEmailRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RedisEmailRepository(redisService);
        ReflectionTestUtils.setField(repository, "defaultTtlMinutes", 5L);
    }

    @Test
    @DisplayName("Должен сохранить код верификации в Redis")
    void shouldSaveVerificationCode() {
        // Given
        String email = "user@example.com";
        var code = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();
        long ttl = 5L;

        // When
        repository.save(code, ttl);

        // Then
        verify(redisService).set(
                eq("email:verification:" + email),
                eq(code),
                eq(Duration.ofMinutes(ttl))
        );
    }

    @Test
    @DisplayName("Должен найти код верификации по email")
    void shouldFindVerificationCodeByEmail() {
        // Given
        String email = "user@example.com";
        var expectedCode = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();

        when(redisService.get(eq("email:verification:" + email), eq(EmailVerificationCode.class)))
                .thenReturn(Optional.of(expectedCode));

        // When
        Optional<EmailVerificationCode> result = repository.findById(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getCode()).isEqualTo("123456");
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional если код не найден")
    void shouldReturnEmptyOptionalIfCodeNotFound() {
        // Given
        String email = "user@example.com";
        when(redisService.get(anyString(), eq(EmailVerificationCode.class)))
                .thenReturn(Optional.empty());

        // When
        Optional<EmailVerificationCode> result = repository.findById(email);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Должен удалить код верификации")
    void shouldDeleteVerificationCode() {
        // Given
        String email = "user@example.com";

        // When
        repository.deleteVerificationCode(email);

        // Then
        verify(redisService).delete("email:verification:" + email);
    }

    @Test
    @DisplayName("Должен инкрементировать попытки верификации")
    void shouldIncrementAttempts() {
        // Given
        String email = "user@example.com";
        var code = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(1)
                .build();

        when(redisService.get(eq("email:verification:" + email), eq(EmailVerificationCode.class)))
                .thenReturn(Optional.of(code));
        when(redisService.getTtl(eq("email:verification:" + email), eq(TimeUnit.MINUTES)))
                .thenReturn(Optional.of(4L));

        // When
        repository.incrementAttempts(email);

        // Then
        verify(redisService).set(
                eq("email:verification:" + email),
                argThat(c -> ((EmailVerificationCode) c).getAttempts() == 2),
                eq(Duration.ofMinutes(4L))
        );
    }

    @Test
    @DisplayName("Должен пометить email как верифицированный")
    void shouldMarkEmailAsVerified() {
        // Given
        String email = "user@example.com";
        long ttl = 10L;

        // When
        repository.markAsVerified(email, ttl);

        // Then
        verify(redisService).set(
                eq("email:verified:" + email),
                eq("verified"),
                eq(Duration.ofMinutes(ttl))
        );
    }

    @Test
    @DisplayName("Должен проверить что email верифицирован")
    void shouldCheckIfEmailIsVerified() {
        // Given
        String email = "user@example.com";
        when(redisService.get(eq("email:verified:" + email), eq(String.class)))
                .thenReturn(Optional.of("verified"));

        // When
        boolean result = repository.isVerified(email);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть false если email не верифицирован")
    void shouldReturnFalseIfEmailNotVerified() {
        // Given
        String email = "user@example.com";
        when(redisService.get(eq("email:verified:" + email), eq(String.class)))
                .thenReturn(Optional.empty());

        // When
        boolean result = repository.isVerified(email);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Должен удалить статус верификации")
    void shouldDeleteVerificationStatus() {
        // Given
        String email = "user@example.com";

        // When
        repository.deleteVerificationStatus(email);

        // Then
        verify(redisService).delete("email:verified:" + email);
    }
}