package com.logistics.userauth.auth.jwt.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.RefreshTokenRequest;
import com.logistics.userauth.user.adapter.in.web.dto.SignInRequest;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureMockMvc
@DisplayName("AuthController: интеграционные тесты")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Регистрация и логин должны работать сквозным сценарием")
    void shouldRegisterAndLogin() throws Exception {
        // 1) Регистрация
        String signUpJson = """
            {
              "email": "test@example.com",
              "phone": "79991234567",
              "password": "Password123!",
              "firstName": "Иван",
              "lastName": "Иванов",
              "middleName": "Иванович"
            }
            """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpJson))
                .andExpect(status().isCreated());

        // 2) Логин
        String signInJson = """
            {
              "phone": "79991234567",
              "password": "Password123!"
            }
            """;

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
        ;
    }

    @Test
    @DisplayName("Должен зарегистрировать пользователя и вернуть токены")
    void shouldRegisterUserAndReturnTokens() throws Exception {
        // Given
        var signUpRequest = new SignUpRequest(
                "newuser@example.com",
                "79991234567",
                "Password123!",
                "John",
                "Doe",
                "Smith"
        );

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(notNullValue()))
                .andExpect(jsonPath("$.refreshToken").value(notNullValue()));
    }

    @Test
    @DisplayName("Должен авторизовать пользователя и вернуть токены")
    void shouldAuthenticateUserAndReturnTokens() throws Exception {
        // Given - сначала регистрируем
        var signUpRequest = new SignUpRequest(
                "logintest@example.com",
                "79997654321",
                "Password123!",
                "Jane",
                "Doe",
                "Smith"
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        // When - логинимся
        var signInRequest = new SignInRequest(
                "79997654321",
                null,
                "Password123!"
        );

        // Then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(notNullValue()))
                .andExpect(jsonPath("$.refreshToken").value(notNullValue()));
    }

    @Test
    @DisplayName("Должен обновить access токен с помощью refresh токена")
    void shouldRefreshAccessToken() throws Exception {
        // Given - регистрируем и получаем токены
        var signUpRequest = new SignUpRequest(
                "refreshtest@example.com",
                "79995555555",
                "Password123!",
                "Refresh",
                "Test",
                "User"
        );

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var authResponse = objectMapper.readTree(responseBody);
        String refreshToken = authResponse.get("refreshToken").asText();

        // When - обновляем токен
        var refreshRequest = new RefreshTokenRequest(refreshToken);

        // Then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(notNullValue()))
                .andExpect(jsonPath("$.refreshToken").value(notNullValue()));
    }

    @Test
    @DisplayName("Должен отозвать refresh токен при logout")
    void shouldRevokeRefreshTokenOnLogout() throws Exception {
        // Given - регистрируем
        var signUpRequest = new SignUpRequest(
                "logouttest@example.com",
                "79996666666",
                "Password123!",
                "Logout",
                "Test",
                "User"
        );

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var authResponse = objectMapper.readTree(responseBody);
        String refreshToken = authResponse.get("refreshToken").asText();

        // When - делаем logout
        var logoutRequest = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isNoContent());

        // Then - пытаемся использовать отозванный токен
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isUnauthorized()); // Должна быть ошибка
    }

    @Test
    @DisplayName("Должен вернуть ошибку для невалидного refresh токена")
    void shouldReturnErrorForInvalidRefreshToken() throws Exception {
        // Given
        var refreshRequest = new RefreshTokenRequest("invalid-token-123");

        // Then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());
    }
}
