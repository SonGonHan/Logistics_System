package com.logistics.userauth.common.web;

import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler: обработка типичных ошибок")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Должен возвращать 401 и INVALID_CREDENTIALS при BadCredentialsException")
    void shouldHandleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        ResponseEntity<Map<String, Object>> response = handler.handleBadCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("INVALID_CREDENTIALS");
        assertThat(response.getBody().get("message")).isEqualTo("Неверный телефон или пароль");
    }

    @Test
    @DisplayName("Должен возвращать 409 и CONFLICT при DataIntegrityViolationException")
    void shouldHandleDataIntegrityViolation() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("duplicate key value violates unique constraint");

        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrity(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("CONFLICT");
        assertThat(response.getBody().get("message"))
                .isEqualTo("Пользователь с таким телефоном или email уже существует");
    }

    @Test
    @DisplayName("Должен возвращать 400 и VALIDATION_FAILED при ошибках Bean Validation")
    void shouldHandleValidationErrors() throws Exception {
        // имитируем DTO с ошибкой в поле "phone"
        class DummyDto {
            @SuppressWarnings("unused")
            private String phone;
        }

        DummyDto target = new DummyDto();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "dummyDto");
        bindingResult.addError(new FieldError("dummyDto", "phone",
                "Неверный формат телефона"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("VALIDATION_FAILED");

        @SuppressWarnings("unchecked")
        Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
        assertThat(fields).containsEntry("phone", "Неверный формат телефона");
    }

    @Test
    @DisplayName("Должен обработать InvalidRefreshTokenException и вернуть 401")
    void shouldHandleInvalidRefreshToken() {
        // Given
        var exception = new InvalidRefreshTokenException("Invalid refresh token");

        // When
        ResponseEntity<Map<String, Object>> response =
                handler.handleInvalidRefreshToken(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("INVALID_REFRESH_TOKEN");
        assertThat(response.getBody().get("message")).isEqualTo("Invalid refresh token");
    }

    @Test
    @DisplayName("Должен обработать InvalidRefreshTokenException с сообщением об истечении срока")
    void shouldHandleExpiredRefreshToken() {
        // Given
        var exception = new InvalidRefreshTokenException("Refresh token has expired");

        // When
        ResponseEntity<Map<String, Object>> response =
                handler.handleInvalidRefreshToken(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("INVALID_REFRESH_TOKEN");
        assertThat(response.getBody().get("message")).isEqualTo("Refresh token has expired");
    }

    @Test
    @DisplayName("Должен обработать InvalidRefreshTokenException с сообщением об отзыве")
    void shouldHandleRevokedRefreshToken() {
        // Given
        var exception = new InvalidRefreshTokenException("Refresh token has been revoked");

        // When
        ResponseEntity<Map<String, Object>> response =
                handler.handleInvalidRefreshToken(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("INVALID_REFRESH_TOKEN");
        assertThat(response.getBody().get("message")).isEqualTo("Refresh token has been revoked");
    }
}
