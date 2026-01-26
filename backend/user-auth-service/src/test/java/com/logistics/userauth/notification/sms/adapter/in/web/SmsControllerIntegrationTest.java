package com.logistics.userauth.notification.sms.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.common.web.GlobalExceptionHandler;
import com.logistics.userauth.notification.sms.adapter.in.web.dto.PhoneVerificationCodeRequest;
import com.logistics.userauth.notification.sms.adapter.in.web.dto.VerifyPhoneRequest;
import com.logistics.userauth.notification.sms.application.port.out.SendSmsPort;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.notification.sms.domain.SmsVerificationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class})
@DisplayName("SmsController: интеграционные тесты")
class SmsControllerIntegrationTest {

    private static final String SEND_URL = "/sms/send-verification-code";
    private static final String VERIFY_URL = "/sms/verify-phone";


    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getFirstMappedPort());

        // Чтобы не ждать 30 секунд в интеграционных тестах
        registry.add("app.sms.verification.resend-cooldown-seconds", () -> "5");

        // Не обязательно (у вас и так default 5), но можно зафиксировать:
        registry.add("app.sms.verification.code-ttl-minutes", () -> "5");

        registry.add("app.sms.verification.max-attempts", () -> "1");
    }


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SendSmsPort sendSmsPort;

    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        when(sendSmsPort.sendVerificationCode(anyString(), anyString())).thenReturn(true);
    }

    @Test
    @DisplayName("Должен успешно отправить код верификации")
    void shouldSendVerificationCode() throws Exception {
        // Given
        var request = new PhoneVerificationCodeRequest("89991234567");

        // When & Then
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Должен вернуть 400 при невалидном номере телефона")
    void shouldReturn400ForInvalidPhone() throws Exception {
        var request = new PhoneVerificationCodeRequest("123");

        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fields.phone").value("Неверный формат телефона"));
    }

    @Test
    @DisplayName("POST /sms/send-verification-code - 429 rate limit")
    void shouldReturn429WhenRateLimitExceeded() throws Exception {
        // Given
        var request = new PhoneVerificationCodeRequest("89991234567");

        // When
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());


        //Then
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    @DisplayName("Должен успешно верифицировать корректный код")
    void shouldVerifyCorrectCode() throws Exception {
        // Given
        String phone = "89991234567";
        String code = "123456";

        var verificationCode = SmsVerificationCode.builder()
                .id(phone)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();
        smsRepository.save(verificationCode, 5L);

        var request = VerifyPhoneRequest.builder()
                .phone(phone)
                .code(code)
                .build();

        // When & Then
        mockMvc.perform(post(VERIFY_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Телефон успешно верифицирован"));
    }

    @Test
    @DisplayName("POST /sms/verify-phone - должен вернуть 400 при неверном коде")
    void shouldReturn400ForInvalidCode() throws Exception {
        // Given
        String phone = "89991234567";

        var verificationCode = SmsVerificationCode.builder()
                .id(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();
        smsRepository.save(verificationCode, 5L);

        var request = VerifyPhoneRequest.builder()
                .phone(phone)
                .code("654321") // Неверный код
                .build();

        // When & Then
        mockMvc.perform(post(VERIFY_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("попыток")));
    }

    @Test
    @DisplayName("POST /sms/verify-phone - должен вернуть 400 при истекшем коде")
    void shouldReturn400ForExpiredCode() throws Exception {
        // Given
        String phone = "89991234567";

        var verificationCode = SmsVerificationCode.builder()
                .id(phone)
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1)) // Истек
                .attempts(0)
                .build();
        smsRepository.save(verificationCode, 5L);

        var request = VerifyPhoneRequest.builder()
                .phone(phone)
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
    @DisplayName("POST /sms/verify-phone - должен вернуть 400 при отсутствии кода")
    void shouldReturn400WhenCodeNotFound() throws Exception {
        // Given
        var request = VerifyPhoneRequest.builder()
                .phone("89991234567")
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
    @DisplayName("POST /sms/send-verification-code - должен вернуть 503 при ошибке отправки SMS")
    void shouldReturn503WhenSmsDeliveryFails() throws Exception {
        // When
        when(sendSmsPort.sendVerificationCode(anyString(), anyString())).thenReturn(false);

        var request = new PhoneVerificationCodeRequest("89991234567");

        // Then
        mockMvc.perform(post(SEND_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("SMS_DELIVERY_FAILED"))
                .andExpect(jsonPath("$.message").value("Не удалось отправить код. Попробуйте позже или обратитесь в поддержку."));
    }


    @Test
    @DisplayName("POST /sms/send-verification-code - должен разрешить resend после cooldown")
    void shouldAllowResendAfterCooldown() throws Exception {
        // Given
        var request = new PhoneVerificationCodeRequest("89991234567");

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
    @DisplayName("POST /sms/send-verification-code - resend после cooldown генерирует новый код")
    void shouldGenerateNewCodeOnResend() throws Exception {
        // Given
        String phone = "89991234567";
        var request = new PhoneVerificationCodeRequest(phone);

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
        verify(sendSmsPort, times(2)).sendVerificationCode(eq(phone), codeCaptor.capture());

        var codes = codeCaptor.getAllValues();
        assertThat(codes.get(0)).isNotEqualTo(codes.get(1));
    }


}
