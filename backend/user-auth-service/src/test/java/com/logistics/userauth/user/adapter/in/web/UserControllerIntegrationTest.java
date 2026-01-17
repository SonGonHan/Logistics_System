package com.logistics.userauth.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.common.web.GlobalExceptionHandler;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.adapter.in.web.dto.UserUpdateRequest;
import com.logistics.userauth.user.application.port.in.GetUserInfoUseCase;
import com.logistics.userauth.user.application.port.in.UpdateUserInfoUseCase;
import com.logistics.userauth.user.application.port.in.command.GetUserInfoCommand;
import com.logistics.userauth.user.application.port.in.command.UpdateUserInfoCommand;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.infrastructure.LogisticsUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("UserController - интеграционный тест")
class UserControllerIntegrationTest {

    private static final String ME_URL = "/users/me";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserInfoUseCase getUserInfoUseCase;

    @MockBean
    private UpdateUserInfoUseCase updateUserInfoUseCase;

    private Authentication authenticationWithUserId(Long userId) {
        var user = User.builder()
                .id(userId)
                .phone("79991234567")
                .role(UserRole.CLIENT)
                .build();

        var principal = new LogisticsUserDetails(user);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    @Test
    @DisplayName("GET /users/me: возвращает профиль текущего пользователя")
    void shouldReturnCurrentUserInfo() throws Exception {
        // Given
        var expected = UserInfoResponse.builder()
                .email("a@b.com")
                .phone("89991234567")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .build();

        when(getUserInfoUseCase.getUserInfo(any(GetUserInfoCommand.class))).thenReturn(expected);

        // When / Then
        mockMvc.perform(get(ME_URL)
                        .principal(authenticationWithUserId(10L))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        var captor = ArgumentCaptor.forClass(GetUserInfoCommand.class);
        verify(getUserInfoUseCase).getUserInfo(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(10L);

        verifyNoInteractions(updateUserInfoUseCase);
    }

    @Test
    @DisplayName("PUT /users/me: обновляет профиль и возвращает обновлённые данные")
    void shouldUpdateUserInfo() throws Exception {
        // Given
        var request = new UserUpdateRequest(
                "new@mail.com",
                "89990000000",
                "New",
                "Name",
                "Mid",
                "OldPass123!",
                "NewPass123!"
        );

        var expected = UserInfoResponse.builder()
                .email("new@mail.com")
                .phone("89990000000")
                .firstName("New")
                .lastName("Name")
                .middleName("Mid")
                .build();

        when(updateUserInfoUseCase.update(any(UpdateUserInfoCommand.class))).thenReturn(expected);

        // When / Then
        mockMvc.perform(put(ME_URL)
                        .principal(authenticationWithUserId(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        var captor = ArgumentCaptor.forClass(UpdateUserInfoCommand.class);
        verify(updateUserInfoUseCase).update(captor.capture());

        var cmd = captor.getValue();
        assertThat(cmd.userId()).isEqualTo(7L);
        assertThat(cmd.email()).isEqualTo("new@mail.com");
        assertThat(cmd.phone()).isEqualTo("89990000000");
        assertThat(cmd.firstName()).isEqualTo("New");
        assertThat(cmd.lastName()).isEqualTo("Name");
        assertThat(cmd.middleName()).isEqualTo("Mid");
        assertThat(cmd.oldPassword()).isEqualTo("OldPass123!");
        assertThat(cmd.newPassword()).isEqualTo("NewPass123!");

        verifyNoInteractions(getUserInfoUseCase);
    }

    @Test
    @DisplayName("PUT /users/me: возвращает 400 при невалидном email (валидация DTO)")
    void shouldReturn400ForInvalidEmail() throws Exception {
        // Given
        var request = new UserUpdateRequest(
                "not-an-email",
                "79990000000",
                "New",
                "Name",
                "Mid",
                null,
                null
        );

        // When / Then
        mockMvc.perform(put(ME_URL)
                        .principal(authenticationWithUserId(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fields.email").exists());

        verifyNoInteractions(updateUserInfoUseCase, getUserInfoUseCase);
    }

    @Test
    @DisplayName("PUT /users/me: возвращает 401, если пользователь не аутентифицирован")
    void shouldReturn401WhenNoAuthentication() throws Exception {
        // Given
        var request = new UserUpdateRequest(
                "a@b.com",
                "89990000000",
                "A",
                "B",
                "C",
                "",
                ""
        );


        // When / Then
        mockMvc.perform(put(ME_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(updateUserInfoUseCase, getUserInfoUseCase);
    }
}
