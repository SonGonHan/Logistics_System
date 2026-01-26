package com.logistics.userauth.notification.email.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.common.web.GlobalExceptionHandler;
import com.logistics.userauth.notification.email.adapter.in.web.dto.EmailVerificationCodeRequest;
import com.logistics.userauth.notification.email.adapter.in.web.dto.VerifyEmailRequest;
import com.logistics.userauth.notification.email.application.port.out.EmailRepository;
import com.logistics.userauth.notification.email.application.port.out.SendEmailPort;
import com.logistics.userauth.notification.email.domain.EmailVerificationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class})
@DisplayName("EmailController: интеграционные тесты")
class EmailControllerIntegrationTest {

    private static final String SEND_URL = "/email/send-verification-code";
    private static final String VERIFY_URL = "/email/verify-email";
    private static final String CONFIG_URL = "/email/config";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getFirstMappedPort());

        // Чтобы не ждать 60 секунд в интеграционных тестах
        registry.add("app.email.verification.resend-cooldown-seconds", () -> "5");
        registry.add("app.email.verification.code-ttl-minutes", () -> "5");
        registry.add("app.email.verification.max-attempts", () -> "1");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SendEmailPort sendEmailPort;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        when(sendEmailPort.sendVerificationCode(anyString(), anyString())).thenReturn(true);
    }

    @Test
    @DisplayName("GET /email/config - должен вернуть конфигурацию")
    void shouldReturnEmailConfig() throws Exception {
        mockMvc.perform(get(CONFIG_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resendCooldownSeconds").value(5));
    }

    @Test
    @DisplayName("POST /email/send-verification-code - должен успешно отправить код верификации")
    void shouldSendVerificationCode() throws Exception {
        // Given
        var request = new EmailVerificationCodeRequest("user@example.com");

        // When & Then
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /email/send-verification-code - должен вернуть 400 при невалидном email")
    void shouldReturn400ForInvalidEmail() throws Exception {
        var request = new EmailVerificationCodeRequest("invalid-email");

        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fields.email").value("Некорректный формат email"));
    }

    @Test
    @DisplayName("POST /email/send-verification-code - должен вернуть 429 при превышении rate limit")
    void shouldReturn429WhenRateLimitExceeded() throws Exception {
        // Given
        var request = new EmailVerificationCodeRequest("user@example.com");

        // When - первая отправка успешна
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then - вторая отправка заблокирована
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /email/verify-email - должен успешно верифицировать корректный код")
    void shouldVerifyCorrectCode() throws Exception {
        // Given
        String email = "user@example.com";
        String code = "123456";

        var verificationCode = EmailVerificationCode.builder()
                .id(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();
        emailRepository.save(verificationCode, 5L);

        var request = VerifyEmailRequest.builder()
                .email(email)
                .code(code)
                .build();

        // When & Then
        mockMvc.perform(post(VERIFY_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email успешно верифицирован"))
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @DisplayName("POST /email/verify-email - должен вернуть 400 при неверном коде")
    void shouldReturn400ForInvalidCode() throws Exception {
        // Given
        String email = "user@example.com";

        var verificationCode = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();
        emailRepository.save(verificationCode, 5L);

        var request = VerifyEmailRequest.builder()
                .email(email)
                .code("654321")
                .build();

        // When & Then
        mockMvc.perform(post(VERIFY_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("попыток")));
    }

    @Test
    @DisplayName("POST /email/verify-email - должен вернуть 400 при истекшем коде")
    void shouldReturn400ForExpiredCode() throws Exception {
        // Given
        String email = "user@example.com";

        var verificationCode = EmailVerificationCode.builder()
                .id(email)
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1)) // Истек
                .attempts(0)
                .build();
        emailRepository.save(verificationCode, 5L);

        var request = VerifyEmailRequest.builder()
                .email(email)
                .code("123456")
                .build();

        // When & Then
        mockMvc.perform(post(VERIFY_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("истек")));
    }

    @Test
    @DisplayName("POST /email/verify-email - должен вернуть 400 при отсутствии кода")
    void shouldReturn400WhenCodeNotFound() throws Exception {
        // Given
        var request = VerifyEmailRequest.builder()
                .email("user@example.com")
                .code("123456")
                .build();

        // When & Then
        mockMvc.perform(post(VERIFY_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /email/send-verification-code - должен вернуть 503 при ошибке отправки Email")
    void shouldReturn503WhenEmailDeliveryFails() throws Exception {
        // When
        when(sendEmailPort.sendVerificationCode(anyString(), anyString())).thenReturn(false);

        var request = new EmailVerificationCodeRequest("user@example.com");

        // Then
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("EMAIL_DELIVERY_FAILED"))
                .andExpect(jsonPath("$.message").value("Не удалось отправить код. Попробуйте позже или обратитесь в поддержку."));
    }

    @Test
    @DisplayName("POST /email/send-verification-code - должен разрешить resend после cooldown")
    void shouldAllowResendAfterCooldown() throws Exception {
        // Given
        var request = new EmailVerificationCodeRequest("user@example.com");

        // When
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Thread.sleep(5100);

        // Then
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /email/send-verification-code - resend после cooldown генерирует новый код")
    void shouldGenerateNewCodeOnResend() throws Exception {
        // Given
        String email = "user@example.com";
        var request = new EmailVerificationCodeRequest(email);

        // When
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Thread.sleep(5100);

        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);

        // Then
        verify(sendEmailPort, times(2)).sendVerificationCode(eq(email.toLowerCase()), codeCaptor.capture());

        var codes = codeCaptor.getAllValues();
        assertThat(codes.get(0)).isNotEqualTo(codes.get(1));
    }

    @Test
    @DisplayName("POST /email/send-verification-code - должен нормализовать email")
    void shouldNormalizeEmail() throws Exception {
        // Given
        var request = new EmailVerificationCodeRequest("User@Example.COM");

        // When
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then
        verify(sendEmailPort).sendVerificationCode(eq("user@example.com"), anyString());
    }
}