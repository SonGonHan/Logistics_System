package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
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
@DisplayName("RegisterUserService: юнит-тесты")
public class RegisterUserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @Mock
    private InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @InjectMocks
    private RegisterUserService service;

    @Test
    @DisplayName("Должен зарегистрировать пользователя и вернуть JWT-токен")
    void shouldRegisterUserAndReturnJwt() {
        // given
        var command = RegisterUserCommand.builder()
                .email("test@example.com")
                .phone("79991234567")
                .rawPassword("rawPass")
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

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
        when(tokenGenerator.generateAccessToken(savedUser)).thenReturn("access-token");
        when(createRefreshTokenUseCase.create(any())).thenReturn("refresh-token");
        // when
        var response = service.register(command);

        // then
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");

        verify(userRepository).save(any(User.class));
        verify(tokenGenerator).generateAccessToken(savedUser);
        verify(createRefreshTokenUseCase).create(any());
    }
}
