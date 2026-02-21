package com.logistics.userauth.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.common.web.GlobalExceptionHandler;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.adapter.in.web.dto.UserPasswordUpdateRequest;
import com.logistics.userauth.user.adapter.in.web.dto.UserPersonalDataUpdateRequest;
import com.logistics.userauth.user.adapter.in.web.dto.UserPhoneUpdateRequest;
import com.logistics.userauth.user.application.port.in.EnsureUserByPhoneUseCase;
import com.logistics.userauth.user.application.port.in.GetUserInfoUseCase;
import com.logistics.userauth.user.application.port.in.UpdateUserPasswordUseCase;
import com.logistics.userauth.user.application.port.in.UpdateUserPersonalInfoUseCase;
import com.logistics.userauth.user.application.port.in.UpdateUserPhoneUseCase;
import com.logistics.userauth.user.application.port.in.command.GetUserInfoCommand;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPasswordCommand;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPersonalInfoCommand;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPhoneCommand;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("UserController - интеграционные тесты")
class UserControllerIntegrationTest {

    private static final String ME_URL = "/users/me";
    private static final String PHONE_URL = "/users/me/phone";
    private static final String PASSWORD_URL = "/users/me/password";
    private static final String PERSONAL_URL = "/users/me/personal";
    private static final String ENSURE_BY_PHONE_URL = "/users/ensure-by-phone";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserInfoUseCase getUserInfoUseCase;

    @MockBean
    private UpdateUserPhoneUseCase updateUserPhoneUseCase;

    @MockBean
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    @MockBean
    private UpdateUserPersonalInfoUseCase updateUserPersonalInfoUseCase;

    @MockBean
    private EnsureUserByPhoneUseCase ensureUserByPhoneUseCase;

    private Authentication authenticationWithUserId(Long userId) {
        var user = User.builder()
                .id(userId)
                .phone("89991234567")
                .role(UserRole.CLIENT)
                .build();
        var principal = new LogisticsUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    // ===================================
    // GET /users/me - Получение профиля
    // ===================================

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

        when(getUserInfoUseCase.getUserInfo(any(GetUserInfoCommand.class)))
                .thenReturn(expected);

        // When / Then
        mockMvc.perform(get(ME_URL)
                        .principal(authenticationWithUserId(10L))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        var captor = ArgumentCaptor.forClass(GetUserInfoCommand.class);
        verify(getUserInfoUseCase).getUserInfo(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(10L);

        verifyNoInteractions(updateUserPhoneUseCase, updateUserPasswordUseCase, updateUserPersonalInfoUseCase);
    }

    // =========================================
    // PUT /users/me/phone - Обновление телефона
    // =========================================

    @Test
    @DisplayName("PUT /users/me/phone: успешно обновляет телефон пользователя")
    void shouldUpdatePhoneSuccessfully() throws Exception {
        // Given
        var request = new UserPhoneUpdateRequest("89990000000");
        var expected = UserInfoResponse.builder()
                .email("user@example.com")
                .phone("89990000000")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .build();

        when(updateUserPhoneUseCase.update(any(UpdateUserPhoneCommand.class)))
                .thenReturn(expected);

        // When / Then
        mockMvc.perform(put(PHONE_URL)
                        .principal(authenticationWithUserId(5L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        var captor = ArgumentCaptor.forClass(UpdateUserPhoneCommand.class);
        verify(updateUserPhoneUseCase).update(captor.capture());

        var command = captor.getValue();
        assertThat(command.userId()).isEqualTo(5L);
        assertThat(command.phone()).isEqualTo("89990000000");

        verifyNoInteractions(getUserInfoUseCase, updateUserPasswordUseCase, updateUserPersonalInfoUseCase);
    }

    @Test
    @DisplayName("PUT /users/me/phone: возвращает 400 при невалидном телефоне")
    void shouldReturn400ForInvalidPhone() throws Exception {
        // Given
        var request = new UserPhoneUpdateRequest("123"); // Невалидный номер

        // When / Then
        mockMvc.perform(put(PHONE_URL)
                        .principal(authenticationWithUserId(5L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(updateUserPhoneUseCase);
    }

    // =========================================
    // PUT /users/me/password - Обновление пароля
    // =========================================

    @Test
    @DisplayName("PUT /users/me/password: успешно обновляет пароль пользователя")
    void shouldUpdatePasswordSuccessfully() throws Exception {
        // Given
        var request = new UserPasswordUpdateRequest("OldPass123!", "NewPass123!");

        doNothing().when(updateUserPasswordUseCase).update(any(UpdateUserPasswordCommand.class));

        // When / Then
        mockMvc.perform(put(PASSWORD_URL)
                        .principal(authenticationWithUserId(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(UpdateUserPasswordCommand.class);
        verify(updateUserPasswordUseCase).update(captor.capture());

        var command = captor.getValue();
        assertThat(command.userId()).isEqualTo(7L);
        assertThat(command.oldPassword()).isEqualTo("OldPass123!");
        assertThat(command.newPassword()).isEqualTo("NewPass123!");

        verifyNoInteractions(getUserInfoUseCase, updateUserPhoneUseCase, updateUserPersonalInfoUseCase);
    }

    @Test
    @DisplayName("PUT /users/me/password: возвращает 400 при невалидном пароле")
    void shouldReturn400ForInvalidPassword() throws Exception {
        // Given
        var request = new UserPasswordUpdateRequest("OldPass123!", "weak"); // Слабый пароль

        // When / Then
        mockMvc.perform(put(PASSWORD_URL)
                        .principal(authenticationWithUserId(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(updateUserPasswordUseCase);
    }

    @Test
    @DisplayName("PUT /users/me/password: возвращает 401 при неверном старом пароле")
    void shouldReturn401ForWrongOldPassword() throws Exception {
        // Given
        var request = new UserPasswordUpdateRequest("WrongPass123!", "NewPass123!");

        doThrow(new BadCredentialsException("Неверный старый пароль"))
                .when(updateUserPasswordUseCase).update(any(UpdateUserPasswordCommand.class));

        // When / Then
        mockMvc.perform(put(PASSWORD_URL)
                        .principal(authenticationWithUserId(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Неверный старый пароль"));

        verify(updateUserPasswordUseCase).update(any(UpdateUserPasswordCommand.class));
    }

    // ==============================================
    // PATCH /users/me/personal - Обновление персональных данных
    // ==============================================

    @Test
    @DisplayName("PATCH /users/me/personal: успешно обновляет персональные данные")
    void shouldUpdatePersonalInfoSuccessfully() throws Exception {
        // Given
        var request = new UserPersonalDataUpdateRequest(
                "Новое",
                "Имя",
                "Отчество",
                "new@example.com"
        );

        var expected = UserInfoResponse.builder()
                .email("new@example.com")
                .phone("89991234567")
                .firstName("Новое")
                .lastName("Имя")
                .middleName("Отчество")
                .build();

        when(updateUserPersonalInfoUseCase.update(any(UpdateUserPersonalInfoCommand.class)))
                .thenReturn(expected);

        // When / Then
        mockMvc.perform(patch(PERSONAL_URL)
                        .principal(authenticationWithUserId(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        var captor = ArgumentCaptor.forClass(UpdateUserPersonalInfoCommand.class);
        verify(updateUserPersonalInfoUseCase).update(captor.capture());

        var command = captor.getValue();
        assertThat(command.userId()).isEqualTo(3L);
        assertThat(command.firstName()).isEqualTo("Новое");
        assertThat(command.lastName()).isEqualTo("Имя");
        assertThat(command.middleName()).isEqualTo("Отчество");
        assertThat(command.email()).isEqualTo("new@example.com");

        verifyNoInteractions(getUserInfoUseCase, updateUserPhoneUseCase, updateUserPasswordUseCase);
    }

    @Test
    @DisplayName("PATCH /users/me/personal: успешно обновляет только email")
    void shouldUpdateOnlyEmail() throws Exception {
        // Given
        var request = new UserPersonalDataUpdateRequest(
                null,
                null,
                null,
                "newemail@example.com"
        );

        var expected = UserInfoResponse.builder()
                .email("newemail@example.com")
                .phone("89991234567")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .build();

        when(updateUserPersonalInfoUseCase.update(any(UpdateUserPersonalInfoCommand.class)))
                .thenReturn(expected);

        // When / Then
        mockMvc.perform(patch(PERSONAL_URL)
                        .principal(authenticationWithUserId(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.phone").value("89991234567"));

        verify(updateUserPersonalInfoUseCase).update(any(UpdateUserPersonalInfoCommand.class));
    }

    @Test
    @DisplayName("PATCH /users/me/personal: возвращает 400 при невалидном email")
    void shouldReturn400ForInvalidEmail() throws Exception {
        // Given
        var request = new UserPersonalDataUpdateRequest(
                "Имя",
                "Фамилия",
                "Отчество",
                "not-an-email" // Невалидный email
        );

        // When / Then
        mockMvc.perform(patch(PERSONAL_URL)
                        .principal(authenticationWithUserId(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(updateUserPersonalInfoUseCase);
    }

    @Test
    @DisplayName("PATCH /users/me/personal: успешно обновляет только имя и фамилию")
    void shouldUpdateOnlyNameFields() throws Exception {
        // Given
        var request = new UserPersonalDataUpdateRequest(
                "Петр",
                "Петров",
                null,
                null
        );

        var expected = UserInfoResponse.builder()
                .email("user@example.com")
                .phone("89991234567")
                .firstName("Петр")
                .lastName("Петров")
                .middleName("Иванович")
                .build();

        when(updateUserPersonalInfoUseCase.update(any(UpdateUserPersonalInfoCommand.class)))
                .thenReturn(expected);

        // When / Then
        mockMvc.perform(patch(PERSONAL_URL)
                        .principal(authenticationWithUserId(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Петр"))
                .andExpect(jsonPath("$.lastName").value("Петров"));

        var captor = ArgumentCaptor.forClass(UpdateUserPersonalInfoCommand.class);
        verify(updateUserPersonalInfoUseCase).update(captor.capture());

        var command = captor.getValue();
        assertThat(command.firstName()).isEqualTo("Петр");
        assertThat(command.lastName()).isEqualTo("Петров");
        assertThat(command.middleName()).isNull();
        assertThat(command.email()).isNull();
    }

    // ===================================
    // Дополнительные граничные случаи
    // ===================================

    // ===================================
    // POST /users/ensure-by-phone
    // ===================================

    @Test
    @DisplayName("POST /users/ensure-by-phone: возвращает 200 с userId")
    void shouldEnsureUserByPhoneSuccessfully() throws Exception {
        // Given
        when(ensureUserByPhoneUseCase.ensure(any())).thenReturn(77L);

        var requestBody = """
                {"phone": "89991234567"}
                """;

        // When / Then
        mockMvc.perform(post(ENSURE_BY_PHONE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(77L));

        var captor = ArgumentCaptor.forClass(
                com.logistics.userauth.user.application.port.in.command.EnsureUserByPhoneCommand.class);
        verify(ensureUserByPhoneUseCase).ensure(captor.capture());
        assertThat(captor.getValue().phone()).isEqualTo("89991234567");
    }

    @Test
    @DisplayName("POST /users/ensure-by-phone: возвращает 400 при пустом номере телефона")
    void shouldReturn400WhenPhoneIsBlank() throws Exception {
        // Given
        var requestBody = """
                {"phone": ""}
                """;

        // When / Then
        mockMvc.perform(post(ENSURE_BY_PHONE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ensureUserByPhoneUseCase);
    }

    @Test
    @DisplayName("Все эндпоинты: возвращают правильный Content-Type")
    void shouldReturnCorrectContentType() throws Exception {
        // Given
        var phoneRequest = new UserPhoneUpdateRequest("89990000000");
        var personalRequest = new UserPersonalDataUpdateRequest("A", "B", "C", "a@b.com");

        var userInfoResponse = UserInfoResponse.builder()
                .email("a@b.com")
                .phone("89990000000")
                .firstName("A")
                .lastName("B")
                .middleName("C")
                .build();

        when(getUserInfoUseCase.getUserInfo(any())).thenReturn(userInfoResponse);
        when(updateUserPhoneUseCase.update(any())).thenReturn(userInfoResponse);
        when(updateUserPersonalInfoUseCase.update(any())).thenReturn(userInfoResponse);

        // When / Then - GET
        mockMvc.perform(get(ME_URL)
                        .principal(authenticationWithUserId(1L)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // When / Then - PUT phone
        mockMvc.perform(put(PHONE_URL)
                        .principal(authenticationWithUserId(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // When / Then - PATCH personal
        mockMvc.perform(patch(PERSONAL_URL)
                        .principal(authenticationWithUserId(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personalRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
