package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для RegisterUserService")
public class RegisterUserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @InjectMocks
    private RegisterUserService service;

    @Test
    @DisplayName("Должен зарегистрировать пользователя и вернуть JWT-токен")
    void shouldRegisterUserAndReturnJwt() {
        // given
        var command = new RegisterUserCommand(
                "test@example.com",
                "79991234567",
                "rawPass",
                "Ivan",
                "Ivanov",
                "Ivanovich"
        );

        var savedUser = User.builder()
                .id(1L)
                .email(command.email())
                .phone(command.phone())
                .passwordHash("encodedPass")
                .firstName(command.firstName())
                .lastName(command.lastName())
                .middleName(command.middleName())
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenGenerator.generateAccessToken(savedUser)).thenReturn("jwt-token");

        // when
        var response = service.register(command);

        // then
        assertEquals("jwt-token", response.token());
        verify(userRepository).save(any(User.class));
        verify(tokenGenerator).generateAccessToken(savedUser);
    }
}
