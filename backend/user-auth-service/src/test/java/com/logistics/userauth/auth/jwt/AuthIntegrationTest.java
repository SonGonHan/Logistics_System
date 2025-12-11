package com.logistics.userauth.auth.jwt;

import com.logistics.userauth.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@IntegrationTest
@AutoConfigureMockMvc
@DisplayName("AuthController: интеграционный тест")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(jsonPath("$.token").exists())
        ;
    }
}
