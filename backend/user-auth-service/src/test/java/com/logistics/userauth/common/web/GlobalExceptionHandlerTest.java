package com.logistics.userauth.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException;
import com.logistics.userauth.notification.common.application.exception.InvalidVerificationCodeException;
import com.logistics.userauth.notification.common.application.exception.RateLimitExceededException;
import com.logistics.userauth.notification.sms.application.exception.SmsDeliveryException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        }
)
@Import({
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.TestController.class
})
@DisplayName("GlobalExceptionHandler - тестирование обработки исключений")
class GlobalExceptionHandlerTest {

    @MockBean
    private CreateAuditLogUseCase createAuditLogUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== КОНФИГУРАЦИЯ ==========

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }


    // ========== ТЕСТЫ ==========

    @Test
    @DisplayName("Должен возвращать 401 и INVALID_CREDENTIALS при BadCredentialsException")
    void shouldHandleBadCredentials() throws Exception {
        mockMvc.perform(post("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Неверный телефон или пароль"));
    }

    @Test
    @DisplayName("Должен возвращать 409 и CONFLICT при дублировании email")
    void shouldHandleDataIntegrityViolationEmail() throws Exception {
        mockMvc.perform(post("/test/data-integrity-email"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Пользователь с таким email уже существует"));
    }

    @Test
    @DisplayName("Должен возвращать 409 и CONFLICT при дублировании phone")
    void shouldHandleDataIntegrityViolationPhone() throws Exception {
        mockMvc.perform(post("/test/data-integrity-phone"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Пользователь с таким телефоном уже существует"));
    }

    @Test
    @DisplayName("Должен возвращать 409 и CONFLICT при других нарушениях уникальности")
    void shouldHandleDataIntegrityViolationGeneric() throws Exception {
        mockMvc.perform(post("/test/data-integrity-generic"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Нарушение уникальности данных"));
    }

    @Test
    @DisplayName("Должен возвращать 400 и VALIDATION_FAILED при ошибках Bean Validation")
    void shouldHandleValidationErrors() throws Exception {
        var invalidRequest = new TestRequest("");

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fields.phone").value("Неверный формат телефона"));
    }

    @Test
    @DisplayName("Должен обработать InvalidRefreshTokenException и вернуть 401")
    void shouldHandleInvalidRefreshToken() throws Exception {
        mockMvc.perform(post("/test/invalid-refresh-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_REFRESH_TOKEN"))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    @DisplayName("Должен обработать RateLimitExceededException и вернуть 429")
    void shouldHandleRateLimitExceeded() throws Exception {
        mockMvc.perform(post("/test/rate-limit"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("RATE_LIMIT_EXCEEDED"))
                .andExpect(jsonPath("$.message").value(containsString("60 сек")));
    }

    @Test
    @DisplayName("Должен обработать SmsDeliveryException и вернуть 500")
    void shouldHandleSmsDeliveryException() throws Exception {
        mockMvc.perform(post("/test/sms-delivery-error"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("SMS_DELIVERY_FAILED"))
                .andExpect(jsonPath("$.message").value("Не удалось отправить SMS. Повторите попытку позже."));
    }

    @Test
    @DisplayName("Должен обработать PhoneNotVerifiedException и вернуть 500")
    void shouldHandlePhoneNotVerifiedException() throws Exception {
        mockMvc.perform(post("/test/phone-not-verified"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PHONE_NOT_VERIFIED"))
                .andExpect(jsonPath("$.message").value("Телефон не верифицирован"));
    }

    @Test
    @DisplayName("Должен обработать InvalidVerificationCodeException и вернуть 400")
    void shouldHandleInvalidVerificationCode() throws Exception {
        mockMvc.perform(post("/test/invalid-verification-code"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_VERIFICATION_CODE"))
                .andExpect(jsonPath("$.message").value("Неверный код верификации"));
    }




    // ========== ТЕСТОВЫЙ КОНТРОЛЛЕР ==========

    @RestController
    static class TestController {

        @PostMapping("/test/bad-credentials")
        public void testBadCredentials() {
            throw new BadCredentialsException("Неверный телефон или пароль");
        }

        @PostMapping("/test/data-integrity-email")
        public void testDataIntegrityEmail() {
            throw new DataIntegrityViolationException("duplicate key constraint on email");
        }

        @PostMapping("/test/data-integrity-phone")
        public void testDataIntegrityPhone() {
            throw new DataIntegrityViolationException("duplicate key constraint on phone");
        }

        @PostMapping("/test/data-integrity-generic")
        public void testDataIntegrityGeneric() {
            throw new DataIntegrityViolationException("duplicate key constraint");
        }

        @PostMapping("/test/validation")
        public void testValidation(@Valid @RequestBody TestRequest request) {
            // Validation происходит автоматически через @Valid
        }

        @PostMapping("/test/invalid-refresh-token")
        public void testInvalidRefreshToken() {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        @PostMapping("/test/rate-limit")
        public void testRateLimit() {
            throw new RateLimitExceededException("Слишком много запросов. Повторите через 60 сек.");
        }

        @PostMapping("/test/invalid-verification-code")
        public void testInvalidVerificationCode() {
            throw new InvalidVerificationCodeException("Неверный код верификации");
        }

        @PostMapping("/test/phone-not-verified")
        public void testPhoneNotVerified() {
            throw new PhoneNotVerifiedException("Телефон не верифицирован");
        }

        @PostMapping("/test/sms-delivery-error")
        public void testSmsDeliveryError() {
            throw new SmsDeliveryException("Не удалось отправить SMS. Повторите попытку позже.");
        }


    }

    // ========== ТЕСТОВЫЕ DTO ==========

    @Data
    @NoArgsConstructor
    static class TestRequest {
        @NotBlank(message = "Неверный формат телефона")
        private String phone;

        public TestRequest(String phone) {
            this.phone = phone;
        }
    }
}
