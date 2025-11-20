package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.adapters.out.persistence.UserEntity;
import com.logistics.userauth.user.adapters.out.persistence.UserPersistenceMapper;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для AuditLogPersistenceMapper")
class AuditLogPersistenceMapperTest {

    @Mock
    private AuditActionTypeMapper actionTypeMapper;

    @Mock
    private UserPersistenceMapper userMapper;

    @InjectMocks
    private AuditLogPersistenceMapper mapper;

    private User testUser;
    private UserEntity testUserEntity;
    private AuditActionType testActionType;
    private AuditActionTypeEntity testActionTypeEntity;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).firstName("Test").lastName("User").role(UserRole.CLIENT).build();
        testUserEntity = UserEntity.builder().id(1L).firstName("Test").lastName("User").role(UserRole.CLIENT).build();
        testActionType = AuditActionType.builder().id((short) 1).actionName("USER_LOGIN").build();
        testActionTypeEntity = AuditActionTypeEntity.builder().id((short) 1).actionName("USER_LOGIN").build();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("status", "active");
        AuditLog domain = AuditLog.builder()
                .id(1L)
                .user(testUser)
                .actionType(testActionType)
                .tableName("users")
                .newValues(newValues)
                .build();
        when(userMapper.toEntity(any(User.class))).thenReturn(testUserEntity);
        when(actionTypeMapper.toEntity(any(AuditActionType.class))).thenReturn(testActionTypeEntity);

        // When
        AuditLogEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUser()).isEqualTo(testUserEntity);
        assertThat(entity.getActionType()).isEqualTo(testActionTypeEntity);
        assertThat(entity.getNewValues()).containsEntry("status", "active");
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("role", "COURIER");
        AuditLogEntity entity = AuditLogEntity.builder()
                .id(2L)
                .user(testUserEntity)
                .actionType(testActionTypeEntity)
                .tableName("users")
                .recordId(100L)
                .newValues(newValues)
                .build();
        when(userMapper.toDomain(any(UserEntity.class))).thenReturn(testUser);
        when(actionTypeMapper.toDomain(any(AuditActionTypeEntity.class))).thenReturn(testActionType);

        // When
        AuditLog domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getUser()).isEqualTo(testUser);
        assertThat(domain.getActionType()).isEqualTo(testActionType);
        assertThat(domain.getNewValues()).containsEntry("role", "COURIER");
    }
}
