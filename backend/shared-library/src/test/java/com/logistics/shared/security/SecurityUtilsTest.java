package com.logistics.shared.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SecurityUtils: юнит-тесты")
class SecurityUtilsTest {

    /**
     * Helper метод для создания JWT токена с заданными claims.
     */
    private Jwt createJwt(String subject, String role, String phone) {
        return Jwt.withTokenValue("fake-token")
                .header("alg", "HS256")
                .subject(subject)
                .claim("role", role)
                .claim("phone", phone)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    /**
     * Helper метод для создания JwtAuthenticationToken.
     */
    private JwtAuthenticationToken createJwtAuthenticationToken(Jwt jwt) {
        return new JwtAuthenticationToken(jwt);
    }

    @Test
    @DisplayName("Должен успешно извлечь userId из JWT subject")
    void shouldExtractUserIdFromJwtSubject() {
        // Given
        Jwt jwt = createJwt("12345", "CLIENT", "+79001234567");
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When
        Long userId = SecurityUtils.extractUserId(auth);

        // Then
        assertThat(userId).isEqualTo(12345L);
    }

    @Test
    @DisplayName("Должен выбросить исключение если authentication null")
    void shouldThrowExceptionWhenAuthenticationIsNull() {
        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractUserId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Authentication is null");
    }

    @Test
    @DisplayName("Должен выбросить исключение если authentication не JwtAuthenticationToken")
    void shouldThrowExceptionWhenAuthenticationIsNotJwt() {
        // Given
        Authentication auth = new TestingAuthenticationToken("user", "password");

        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractUserId(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected JwtAuthenticationToken");
    }

    @Test
    @DisplayName("Должен выбросить исключение если subject не является числом")
    void shouldThrowExceptionWhenSubjectIsNotNumeric() {
        // Given
        Jwt jwt = createJwt("not-a-number", "CLIENT", "+79001234567");
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractUserId(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid userId in JWT subject");
    }

    @Test
    @DisplayName("Должен выбросить исключение если subject пустой")
    void shouldThrowExceptionWhenSubjectIsBlank() {
        // Given
        Jwt jwt = createJwt("   ", "CLIENT", "+79001234567");
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractUserId(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT subject (sub) claim is missing or empty");
    }

    @Test
    @DisplayName("Должен успешно извлечь роль из JWT claim 'role'")
    void shouldExtractRoleFromJwtClaim() {
        // Given
        Jwt jwt = createJwt("12345", "EMPLOYEE", "+79001234567");
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When
        String role = SecurityUtils.extractRole(auth);

        // Then
        assertThat(role).isEqualTo("EMPLOYEE");
    }

    @Test
    @DisplayName("Должен выбросить исключение если claim 'role' отсутствует")
    void shouldThrowExceptionWhenRoleClaimIsMissing() {
        // Given
        Jwt jwt = Jwt.withTokenValue("fake-token")
                .header("alg", "HS256")
                .subject("12345")
                .claim("phone", "+79001234567")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractRole(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT 'role' claim is missing or empty");
    }

    @Test
    @DisplayName("Должен выбросить исключение если claim 'role' пустой")
    void shouldThrowExceptionWhenRoleClaimIsBlank() {
        // Given
        Jwt jwt = createJwt("12345", "   ", "+79001234567");
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractRole(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT 'role' claim is missing or empty");
    }

    @Test
    @DisplayName("Должен успешно извлечь телефон из JWT claim 'phone'")
    void shouldExtractPhoneFromJwtClaim() {
        // Given
        Jwt jwt = createJwt("12345", "CLIENT", "+79001234567");
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When
        String phone = SecurityUtils.extractPhone(auth);

        // Then
        assertThat(phone).isEqualTo("+79001234567");
    }

    @Test
    @DisplayName("Должен выбросить исключение если claim 'phone' отсутствует")
    void shouldThrowExceptionWhenPhoneClaimIsMissing() {
        // Given
        Jwt jwt = Jwt.withTokenValue("fake-token")
                .header("alg", "HS256")
                .subject("12345")
                .claim("role", "CLIENT")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractPhone(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT 'phone' claim is missing or empty");
    }

    @Test
    @DisplayName("Должен выбросить исключение если claim 'phone' пустой")
    void shouldThrowExceptionWhenPhoneClaimIsBlank() {
        // Given
        Jwt jwt = createJwt("12345", "CLIENT", "   ");
        Authentication auth = createJwtAuthenticationToken(jwt);

        // When / Then
        assertThatThrownBy(() -> SecurityUtils.extractPhone(auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT 'phone' claim is missing or empty");
    }
}
