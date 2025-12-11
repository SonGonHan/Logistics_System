package com.logistics.userauth.auth.jwt.application;


import com.logistics.userauth.auth.jwt.adapter.out.JwtTokenProvider;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("JwtTokenProvider: юнит‑тесты")
public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        TestReflection.setField(jwtTokenProvider, "secret", "test-secret-key-test-secret-key-123456");
        TestReflection.setField(jwtTokenProvider, "accessTokenTtlSeconds", 3600_000L);
    }

    private User buildUser() {
        return User.builder()
                .id(42L)
                .email("test@example.com")
                .phone("79991234567")
                .passwordHash("HASH")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .lastAccessedTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Должен генерировать и валидировать JWT и извлекать userId")
    void shouldGenerateAndValidateToken() {
        User user = buildUser();

        String token = jwtTokenProvider.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.isTokenValid(token)).isTrue();

        Long userId = jwtTokenProvider.extractUserId(token);
        assertThat(userId).isEqualTo(42L);
    }

    /**
     * Вспомогательный класс для установки приватных полей через reflection,
     * чтобы не тянуть Spring в юнит‑тест.
     */
    static class TestReflection {
        static void setField(Object target, String fieldName, Object value) {
            try {
                var field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
