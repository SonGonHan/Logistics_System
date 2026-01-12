package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.adapter.in.web.dto.JwtAuthenticationResponse;
import com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException;
import com.logistics.userauth.auth.jwt.application.port.in.InternalCreateRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.RegisterUserCommand;
import com.logistics.userauth.auth.jwt.application.port.out.TokenGeneratorPort;
import com.logistics.userauth.sms.application.port.out.SmsRepository;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserService - тестирование регистрации пользователя")
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SmsRepository smsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @Mock
    private InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @InjectMocks
    private RegisterUserService service;

    private RegisterUserCommand validCommand;
    private User savedUser;

    @BeforeEach
    void setUp() {
        validCommand = RegisterUserCommand.builder()
                .email("test@example.com")
                .phone("79991234567")
                .rawPassword("Password123!")
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        savedUser = User.builder()
                .id(1L)
                .email(validCommand.email())
                .phone(validCommand.phone())
                .passwordHash("encodedPass")
                .firstName(validCommand.firstName())
                .lastName(validCommand.lastName())
                .middleName(validCommand.middleName())
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Должен успешно зарегистрировать пользователя и вернуть JWT токены")
    void shouldRegisterUserAndReturnJwt() {
        // Given
        when(smsRepository.isPhoneVerified(validCommand.phone())).thenReturn(true);
        when(passwordEncoder.encode(validCommand.rawPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenGenerator.generateAccessToken(savedUser)).thenReturn("access-token");
        when(createRefreshTokenUseCase.create(any())).thenReturn("refresh-token");

        // When
        JwtAuthenticationResponse response = service.register(validCommand);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");

        // Verify interactions
        verify(smsRepository).isPhoneVerified(validCommand.phone());
        verify(passwordEncoder).encode(validCommand.rawPassword());
        verify(userRepository).save(any(User.class));
        verify(smsRepository).deleteVerificationStatus(validCommand.phone());
        verify(tokenGenerator).generateAccessToken(savedUser);
        verify(createRefreshTokenUseCase).create(any());
    }

    @Test
    @DisplayName("Должен выбросить PhoneNotVerifiedException если телефон не верифицирован")
    void shouldThrowPhoneNotVerifiedExceptionWhenPhoneNotVerified() {
        // Given
        when(smsRepository.isPhoneVerified(validCommand.phone())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.register(validCommand))
                .isInstanceOf(PhoneNotVerifiedException.class);

        // Verify
        verify(smsRepository).isPhoneVerified(validCommand.phone());
        verify(userRepository, never()).save(any());
        verify(tokenGenerator, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Должен выбросить DataIntegrityViolationException при дублировании телефона")
    void shouldThrowDataIntegrityViolationExceptionWhenPhoneDuplicated() {
        // Given
        when(smsRepository.isPhoneVerified(validCommand.phone())).thenReturn(true);
        when(passwordEncoder.encode(validCommand.rawPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate phone"));

        // When & Then
        assertThatThrownBy(() -> service.register(validCommand))
                .isInstanceOf(DataIntegrityViolationException.class);

        // Verify
        verify(smsRepository).isPhoneVerified(validCommand.phone());
        verify(userRepository).save(any(User.class));
        verify(tokenGenerator, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Должен правильно сохранить пользователя с захешированным паролем")
    void shouldSaveUserWithEncodedPassword() {
        // Given
        when(smsRepository.isPhoneVerified(validCommand.phone())).thenReturn(true);
        when(passwordEncoder.encode(validCommand.rawPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenGenerator.generateAccessToken(any())).thenReturn("access-token");
        when(createRefreshTokenUseCase.create(any())).thenReturn("refresh-token");

        // When
        service.register(validCommand);

        // Then
        verify(passwordEncoder).encode("Password123!");
        verify(userRepository).save(argThat(user ->
                user.getPhone().equals("79991234567") &&
                        user.getPasswordHash().equals("encodedPass") &&
                        user.getFirstName().equals("Ivan") &&
                        user.getLastName().equals("Ivanov") &&
                        user.getMiddleName().equals("Ivanovich") &&
                        user.getEmail().equals("test@example.com") &&
                        user.getRole().equals(UserRole.CLIENT) &&
                        user.getStatus().equals(UserStatus.ACTIVE)
        ));
    }

    @Test
    @DisplayName("Должен удалить статус верификации после успешной регистрации")
    void shouldDeleteVerificationStatusAfterRegistration() {
        // Given
        when(smsRepository.isPhoneVerified(validCommand.phone())).thenReturn(true);
        when(passwordEncoder.encode(validCommand.rawPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenGenerator.generateAccessToken(any())).thenReturn("access-token");
        when(createRefreshTokenUseCase.create(any())).thenReturn("refresh-token");

        // When
        service.register(validCommand);

        // Then
        verify(smsRepository).deleteVerificationStatus(validCommand.phone());
    }

    @Test
    @DisplayName("Должен корректно обработать пользователя без email")
    void shouldHandleUserWithoutEmail() {
        // Given
        RegisterUserCommand commandWithoutEmail = RegisterUserCommand.builder()
                .email(null)  // ← email = null
                .phone("79991234567")
                .rawPassword("Password123!")
                .firstName("John")
                .lastName("Doe")
                .middleName(null)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();

        User userWithoutEmail = User.builder()
                .id(1L)
                .email(null)
                .phone("79991234567")
                .passwordHash("encodedPass")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        when(smsRepository.isPhoneVerified(commandWithoutEmail.phone())).thenReturn(true);
        when(passwordEncoder.encode(commandWithoutEmail.rawPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(userWithoutEmail);
        when(tokenGenerator.generateAccessToken(any())).thenReturn("access-token");
        when(createRefreshTokenUseCase.create(any())).thenReturn("refresh-token");

        // When
        JwtAuthenticationResponse response = service.register(commandWithoutEmail);

        // Then
        assertThat(response).isNotNull();
        verify(userRepository).save(argThat(user -> user.getEmail() == null));
    }
}
