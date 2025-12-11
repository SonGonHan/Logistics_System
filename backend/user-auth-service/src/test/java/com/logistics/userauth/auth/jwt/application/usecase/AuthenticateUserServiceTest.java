package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.AuthenticateUserCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для AuthenticateUserService")
public class AuthenticateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @InjectMocks
    private AuthenticateUserService service;

    private User buildUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .phone("79991234567")
                .passwordHash("HASH")
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .lastAccessedTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Должен аутентифицировать пользователя и вернуть JWT")
    void shouldAuthenticateUserAndReturnJwt() {
        // given
        var command = new AuthenticateUserCommand(
                "79991234567",
                "Password123!"
        );

        var user = buildUser();

        when(userRepository.findByPhone("79991234567")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "HASH")).thenReturn(true);
        when(tokenGenerator.generateAccessToken(any(User.class))).thenReturn("jwt-token");

        // when
        JwtAuthenticationResponse response = service.authenticate(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt-token");
    }

    @Test
    @DisplayName("Должен бросить BadCredentialsException при неверном пароле")
    void shouldThrowWhenPasswordInvalid() {
        // given
        var command = new AuthenticateUserCommand(
                "79991234567",
                "wrong"
        );

        var user = buildUser();

        when(userRepository.findByPhone("79991234567")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

        // then
        assertThatThrownBy(() -> service.authenticate(command))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Должен бросить BadCredentialsException, если пользователь не найден")
    void shouldThrowWhenUserNotFound() {
        // given
        var command = new AuthenticateUserCommand(
                "79990000000",
                "Password123!"
        );

        when(userRepository.findByPhone("79990000000")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.authenticate(command))
                .isInstanceOf(BadCredentialsException.class);
    }
}
