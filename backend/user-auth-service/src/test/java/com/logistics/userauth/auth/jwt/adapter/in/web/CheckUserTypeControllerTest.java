package com.logistics.userauth.auth.jwt.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.CheckUserTypeRequest;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@DisplayName("CheckUserType Endpoint - интеграционный тест")
class CheckUserTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Должен вернуть userExists=false, isClient=false для несуществующего пользователя (телефон)")
    void shouldReturnNotExistsForNewUserByPhone() throws Exception {
        // Given
        var request = CheckUserTypeRequest.builder()
                .identifier("+79991112233")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(false))
                .andExpect(jsonPath("$.isClient").value(false));
    }

    @Test
    @DisplayName("Должен вернуть userExists=false, isClient=false для несуществующего пользователя (email)")
    void shouldReturnNotExistsForNewUserByEmail() throws Exception {
        // Given
        var request = CheckUserTypeRequest.builder()
                .identifier("newuser@example.com")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(false))
                .andExpect(jsonPath("$.isClient").value(false));
    }

    @Test
    @DisplayName("Должен вернуть userExists=true, isClient=true для существующего клиента (по телефону)")
    void shouldReturnExistsAndClientForExistingClient() throws Exception {
        // Given - создаем клиента
        String phone = "89991234567";
        User client = User.builder()
                .phone(phone)
                .email("client@example.com")
                .passwordHash(passwordEncoder.encode("Password123!"))
                .firstName("Иван")
                .lastName("Клиентов")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
        userRepository.save(client);

        var request = CheckUserTypeRequest.builder()
                .identifier(phone)
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(true))
                .andExpect(jsonPath("$.isClient").value(true));
    }

    @Test
    @DisplayName("Должен вернуть userExists=true, isClient=true для существующего клиента (по email)")
    void shouldReturnExistsAndClientForExistingClientByEmail() throws Exception {
        // Given - создаем клиента
        String email = "anotherclient@example.com";
        User client = User.builder()
                .phone("89997654321")
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .firstName("Петр")
                .lastName("Клиентов")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
        userRepository.save(client);

        var request = CheckUserTypeRequest.builder()
                .identifier(email)
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(true))
                .andExpect(jsonPath("$.isClient").value(true));
    }

    @Test
    @DisplayName("Должен вернуть userExists=true, isClient=false для сотрудника (WAREHOUSE_OPERATOR)")
    void shouldReturnExistsAndNotClientForStaffMember() throws Exception {
        // Given - создаем сотрудника
        String phone = "89998887766";
        User staff = User.builder()
                .phone(phone)
                .email("warehouse@example.com")
                .passwordHash(passwordEncoder.encode("StaffPass123!"))
                .firstName("Сергей")
                .lastName("Складской")
                .role(UserRole.WAREHOUSE_OPERATOR)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
        userRepository.save(staff);

        var request = CheckUserTypeRequest.builder()
                .identifier(phone)
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(true))
                .andExpect(jsonPath("$.isClient").value(false));
    }

    @Test
    @DisplayName("Должен вернуть userExists=true, isClient=false для администратора (SYSTEM_ADMIN)")
    void shouldReturnExistsAndNotClientForAdmin() throws Exception {
        // Given - создаем администратора
        String email = "admin@example.com";
        User admin = User.builder()
                .phone("89995554433")
                .email(email)
                .passwordHash(passwordEncoder.encode("AdminPass123!"))
                .firstName("Анна")
                .lastName("Админова")
                .role(UserRole.SYSTEM_ADMIN)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
        userRepository.save(admin);

        var request = CheckUserTypeRequest.builder()
                .identifier(email)
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(true))
                .andExpect(jsonPath("$.isClient").value(false));
    }

    @Test
    @DisplayName("Должен вернуть userExists=true, isClient=false для диспетчера (DISPATCHER)")
    void shouldReturnExistsAndNotClientForDispatcher() throws Exception {
        // Given - создаем диспетчера
        String phone = "89996665544";
        User dispatcher = User.builder()
                .phone(phone)
                .email("dispatcher@example.com")
                .passwordHash(passwordEncoder.encode("DispatchPass123!"))
                .firstName("Дмитрий")
                .lastName("Диспетчеров")
                .role(UserRole.DISPATCHER)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
        userRepository.save(dispatcher);

        var request = CheckUserTypeRequest.builder()
                .identifier(phone)
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(true))
                .andExpect(jsonPath("$.isClient").value(false));
    }

    @Test
    @DisplayName("Должен вернуть 400 для пустого identifier")
    void shouldReturn400ForEmptyIdentifier() throws Exception {
        // Given
        var request = CheckUserTypeRequest.builder()
                .identifier("")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 для null identifier")
    void shouldReturn400ForNullIdentifier() throws Exception {
        // Given
        var request = CheckUserTypeRequest.builder()
                .identifier(null)
                .build();

        // When & Then
        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен корректно работать с телефоном в разных форматах")
    void shouldHandleDifferentPhoneFormats() throws Exception {
        // Given - создаем клиента с нормализованным телефоном
        String normalizedPhone = "89993332211";
        User client = User.builder()
                .phone(normalizedPhone)
                .email("format@example.com")
                .passwordHash(passwordEncoder.encode("Password123!"))
                .firstName("Тест")
                .lastName("Форматов")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
        userRepository.save(client);

        // When & Then - проверяем с форматированным телефоном
        var request = CheckUserTypeRequest.builder()
                .identifier("+7 (999) 333-22-11")
                .build();

        mockMvc.perform(post("/auth/check-user-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExists").value(true))
                .andExpect(jsonPath("$.isClient").value(true));
    }
}