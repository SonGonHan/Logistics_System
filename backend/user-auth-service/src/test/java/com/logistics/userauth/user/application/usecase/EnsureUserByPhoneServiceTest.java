package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.application.port.in.command.EnsureUserByPhoneCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnsureUserByPhoneService - поиск или создание пользователя по телефону")
class EnsureUserByPhoneServiceTest {

    @Mock
    private UserRepository userRepository;

    private EnsureUserByPhoneService service;

    @BeforeEach
    void setUp() {
        service = new EnsureUserByPhoneService(userRepository);
    }

    @Test
    @DisplayName("Должен вернуть ID существующего пользователя без создания нового")
    void shouldReturnExistingUserIdWithoutCreating() {
        // Given
        var existingUser = User.builder()
                .id(42L)
                .phone("89991234567")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByPhone("89991234567")).thenReturn(Optional.of(existingUser));

        var command = new EnsureUserByPhoneCommand("89991234567");

        // When
        var result = service.ensure(command);

        // Then
        assertThat(result).isEqualTo(42L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен создать нового пользователя и вернуть его ID, если телефон не найден")
    void shouldCreateNewUserAndReturnIdWhenPhoneNotFound() {
        // Given
        var newUser = User.builder()
                .id(99L)
                .phone("89997654321")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByPhone("89997654321"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(newUser));

        var command = new EnsureUserByPhoneCommand("89997654321");

        // When
        var result = service.ensure(command);

        // Then
        assertThat(result).isEqualTo(99L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Должен создать пользователя с ролью CLIENT и статусом ACTIVE")
    void shouldCreateUserWithClientRoleAndActiveStatus() {
        // Given
        var savedUser = User.builder()
                .id(55L)
                .phone("89990000000")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByPhone("89990000000"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(savedUser));

        var command = new EnsureUserByPhoneCommand("89990000000");

        // When
        service.ensure(command);

        // Then
        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        var created = captor.getValue();
        assertThat(created.getRole()).isEqualTo(UserRole.CLIENT);
        assertThat(created.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(created.getPhone()).isEqualTo("89990000000");
        assertThat(created.getCreatedTime()).isNotNull();
    }
}