package com.logistics.userauth.audit.application.usecase;

import com.logistics.shared.audit_action.AuditActionTypeService;
import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.userauth.audit.application.port.out.AuditLogRepository;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditActionTypeService auditActionTypeService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateAuditLogService createAuditLogService;

    private User testUser;
    private AuditActionType userLoginSuccessType;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id(1L)
                .phone("+79001234567")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        // Setup action type
        userLoginSuccessType = AuditActionType.builder()
                .id((short) 2)
                .actionName("USER_LOGIN_SUCCESS")
                .category("Authentication")
                .description("Успешный вход в систему")
                .build();

        // Mock action type service responses for @PostConstruct
        when(auditActionTypeService.getActionTypeActionName("USER_REGISTER"))
                .thenReturn(Optional.of(createActionType(1, "USER_REGISTER")));
        when(auditActionTypeService.getActionTypeActionName("USER_LOGIN_SUCCESS"))
                .thenReturn(Optional.of(userLoginSuccessType));
        when(auditActionTypeService.getActionTypeActionName("USER_LOGIN_FAILURE"))
                .thenReturn(Optional.of(createActionType(3, "USER_LOGIN_FAILURE")));
        when(auditActionTypeService.getActionTypeActionName("USER_LOGOUT"))
                .thenReturn(Optional.of(createActionType(4, "USER_LOGOUT")));
        when(auditActionTypeService.getActionTypeActionName("PASSWORD_CHANGE"))
                .thenReturn(Optional.of(createActionType(5, "PASSWORD_CHANGE")));
        when(auditActionTypeService.getActionTypeActionName("SESSION_CREATE"))
                .thenReturn(Optional.of(createActionType(7, "SESSION_CREATE")));
        when(auditActionTypeService.getActionTypeActionName("SESSION_REVOKE"))
                .thenReturn(Optional.of(createActionType(8, "SESSION_REVOKE")));
        when(auditActionTypeService.getActionTypeActionName("TOKEN_REFRESH"))
                .thenReturn(Optional.of(createActionType(9, "TOKEN_REFRESH")));
        when(auditActionTypeService.getActionTypeActionName("USER_UPDATE"))
                .thenReturn(Optional.of(createActionType(11, "USER_UPDATE")));

        // Initialize cache
        createAuditLogService.initActionTypeCache();
    }

    @Test
    void shouldCreateAuditLogWithValidCommand() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        var command = new CreateAuditLogCommand(
                1L,
                "USER_LOGIN_SUCCESS",
                "+79001234567",
                "192.168.1.1",
                "Mozilla/5.0",
                Map.of("userId", 1L),
                null,
                null
        );

        // When
        createAuditLogService.create(command);

        // Then
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertThat(savedLog.getUser()).isEqualTo(testUser);
        assertThat(savedLog.getActionType().getActionName()).isEqualTo("USER_LOGIN_SUCCESS");
        assertThat(savedLog.getActorIdentifier()).isEqualTo("+79001234567");
        assertThat(savedLog.getIpAddress()).isNotNull();
        assertThat(savedLog.getNewValues()).containsEntry("userId", 1L);
    }

    @Test
    void shouldHandleNullUserId() {
        // Given: USER_LOGIN_FAILURE с null userId
        var command = new CreateAuditLogCommand(
                null, // Unknown user
                "USER_LOGIN_FAILURE",
                "+79001234567",
                "192.168.1.1",
                "Mozilla/5.0",
                Map.of("attemptedPhone", "+79001234567", "reason", "INVALID_CREDENTIALS"),
                null,
                null
        );

        // When
        createAuditLogService.create(command);

        // Then: Не должно быть вызова userRepository.findById
        verify(userRepository, never()).findById(any());

        // Audit log должен быть сохранён с user = null
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertThat(savedLog.getUser()).isNull();
        assertThat(savedLog.getActorIdentifier()).isEqualTo("+79001234567");
        assertThat(savedLog.getNewValues()).containsEntry("attemptedPhone", "+79001234567");
    }

    @Test
    void shouldNotThrowWhenAuditFails() {
        // Given: Repository выбрасывает исключение
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Database error")).when(auditLogRepository).save(any());

        var command = new CreateAuditLogCommand(
                1L,
                "USER_LOGIN_SUCCESS",
                "+79001234567",
                "192.168.1.1",
                "Mozilla/5.0",
                Map.of("userId", 1L),
                null,
                null
        );

        // When / Then: Не должно выбрасывать исключение
        createAuditLogService.create(command);

        // Verify что save был вызван (но failed)
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldHandleNullIpAddress() {
        // Given: null IP адрес
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        var command = new CreateAuditLogCommand(
                1L,
                "USER_LOGIN_SUCCESS",
                "+79001234567",
                null,
                "Mozilla/5.0",
                Map.of("userId", 1L),
                null,
                null
        );

        // When
        createAuditLogService.create(command);

        // Then: Должен сохранить лог с ipAddress = null
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertThat(savedLog.getIpAddress()).isNull();
    }

    private AuditActionType createActionType(int id, String actionName) {
        return AuditActionType.builder()
                .id((short) id)
                .actionName(actionName)
                .category("TestCategory")
                .description("Test description")
                .build();
    }
}