package com.logistics.userauth.auth.jwt.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.RefreshTokenRequest;
import com.logistics.userauth.sms.application.port.out.SendSmsPort;
import com.logistics.userauth.sms.application.port.out.SmsRepository;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@DisplayName("AuthController - интеграционный тест")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SendSmsPort sendSmsPort;

    @Autowired
    private SmsRepository smsRepository;

    @BeforeEach
    void setUp() {
        // Mock SMS отправки
        when(sendSmsPort.sendVerificationCode(anyString(), anyString())).thenReturn(true);
    }

    @Test
    @DisplayName("Должен успешно зарегистрировать пользователя и вернуть JWT токены")
    void shouldRegisterUserAndReturnTokens() throws Exception {
        // Given - верифицируем телефон
        String phone = "+79991111111";
        String normalizedPhone = PhoneUtils.normalize(phone);
        smsRepository.markPhoneAsVerified(normalizedPhone, 10L);

        // When
        var signUpRequest = new SignUpRequest(
                "test@example.com",
                phone,
                "Password123!",
                "John",
                "Doe",
                "Smith"
        );

        // Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(notNullValue()))
                .andExpect(jsonPath("$.refreshToken").value(notNullValue()));
    }

    @Test
    @DisplayName("Должен вернуть 400 если телефон не верифицирован")
    void shouldReturn400WhenPhoneNotVerified() throws Exception {
        // Given
        var signUpRequest = new SignUpRequest(
                "notverified@example.com",
                "+79992222222",
                "Password123!",
                "Jane",
                "Doe",
                null
        );

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PHONE_NOT_VERIFIED"));
    }

    @Test
    @DisplayName("Должен аутентифицировать пользователя и вернуть токены")
    void shouldAuthenticateUserAndReturnTokens() throws Exception {
        // Given - регистрируем пользователя
        String phone = "+79997654321";
        String normalizedPhone = PhoneUtils.normalize(phone);
        String password = "Password123!";

        // Верифицируем телефон
        smsRepository.markPhoneAsVerified(normalizedPhone, 10L);

        var signUpRequest = new SignUpRequest(
                "login@example.com",
                phone,
                password,
                "Jane",
                "Doe",
                "Smith"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        // When - логинимся
        var signInRequest = new SignInRequest(phone, null, password);

        // Then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(notNullValue()))
                .andExpect(jsonPath("$.refreshToken").value(notNullValue()));
    }

    @Test
    @DisplayName("Должен обновить access token используя refresh token")
    void shouldRefreshAccessToken() throws Exception {
        // Given - регистрируем пользователя
        String phone = "+79995555555";
        String normalizedPhone = PhoneUtils.normalize(phone);
        smsRepository.markPhoneAsVerified(normalizedPhone, 10L);

        var signUpRequest = new SignUpRequest(
                "refresh@example.com",
                phone,
                "Password123!",
                "Refresh",
                "Test",
                "User"
        );

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var authResponse = objectMapper.readTree(responseBody);

        if (authResponse.get("refreshToken") == null) {
            throw new AssertionError("refreshToken не найден в ответе: " + responseBody);
        }

        String refreshToken = authResponse.get("refreshToken").asText();

        // When - обновляем токен
        var refreshRequest = new RefreshTokenRequest(refreshToken);

        // Then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(notNullValue()))
                .andExpect(jsonPath("$.refreshToken").value(notNullValue()));
    }

    @Test
    @DisplayName("Должен успешно выполнить logout и отозвать refresh token")
    void shouldRevokeRefreshTokenOnLogout() throws Exception {
        // Given - регистрируем пользователя
        String phone = "+79996666666";
        String normalizedPhone = PhoneUtils.normalize(phone);
        smsRepository.markPhoneAsVerified(normalizedPhone, 10L);

        var signUpRequest = new SignUpRequest(
                "logout@example.com",
                phone,
                "Password123!",
                "Logout",
                "Test",
                "User"
        );

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var authResponse = objectMapper.readTree(responseBody);
        String refreshToken = authResponse.get("refreshToken").asText();

        // When - делаем logout
        var logoutRequest = new RefreshTokenRequest(refreshToken);
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Then - токен больше не работает
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Должен вернуть 401 при неверном пароле")
    void shouldReturn401ForInvalidPassword() throws Exception {
        // Given - регистрируем пользователя
        String phone = "+79993333333";
        String normalizedPhone = PhoneUtils.normalize(phone);
        smsRepository.markPhoneAsVerified(normalizedPhone, 10L);

        var signUpRequest = new SignUpRequest(
                "invalid@example.com",
                phone,
                "Password123!",
                "Invalid",
                "Test",
                null
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        // When - логинимся с неверным, но валидным паролем
        var signInRequest = new SignInRequest(phone, null, "WrongPassword1!");

        // Then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("Должен вернуть 409 при дублировании телефона")
    void shouldReturn409ForDuplicatePhone() throws Exception {
        // Given - регистрируем первого пользователя
        String phone = "+79994444444";
        String normalizedPhone = PhoneUtils.normalize(phone);
        smsRepository.markPhoneAsVerified(normalizedPhone, 10L);

        var firstRequest = new SignUpRequest(
                "first@example.com",
                phone,
                "Password123!",
                "First",
                "User",
                null
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // ВАЖНО: после успешной регистрации сервис удаляет verification status
        smsRepository.markPhoneAsVerified(normalizedPhone, 10L);

        var secondRequest = new SignUpRequest(
                "second@example.com",
                phone,
                "Password456!",
                "Second",
                "User",
                null
        );

        // Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }
}
