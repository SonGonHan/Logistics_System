package com.logistics.userauth.audit.adapter.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionJpaRepository;
import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.IntegrationTest;
import com.logistics.userauth.user.adapter.out.persistence.UserEntity;
import com.logistics.userauth.user.adapter.out.persistence.UserJpaRepository;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("AuditLogJpaRepository: интеграционные тесты")
class AuditLogJpaRepositoryIntegrationTest {
    @Autowired
    private AuditLogJpaRepository repository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private AuditActionJpaRepository actionTypeRepository;

    private UserEntity testUser;
    private AuditActionTypeEntity testActionType;

    @BeforeEach
    void setUp() {
        // Создаём тестового пользователя
        testUser = UserEntity.builder()
                .phone("+79991111111")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        testUser = userRepository.save(testUser);

        // Создаём тип действия
        testActionType = AuditActionTypeEntity.builder()
                .actionName("TEST_ACTION")
                .category("TEST")
                .description("Test action")
                .build();

        testActionType = actionTypeRepository.save(testActionType);
    }

    @Test
    @Transactional
    @DisplayName("Должен сохранить и найти лог аудита по пользователю")
    void shouldSaveAndFindByUser() {
        // Given
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("field", "value");

        Inet in = new Inet("192.168.1.1");

        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(newValues)
                .performedAt(LocalDateTime.now())
                .ipAddress(in)
                .build();

        // When
        repository.save(auditLog);

        // Then
        List<AuditLogEntity> found = repository.findByUser(testUser);

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getActionType()).isEqualTo(testActionType);
        assertThat(found.get(0).getTableName()).isEqualTo("test_table");
        assertThat(found.get(0).getRecordId()).isEqualTo(1L);
        assertThat(found.get(0).getActorIdentifier()).isEqualTo("test@example.com");
        assertThat(found.get(0).getNewValues()).isEqualTo(newValues);
        assertThat(found.get(0).getIpAddress()).isEqualTo(in);
    }

    @Test
    @DisplayName("Должен сохранить лог аудита с корректными данными")
    void shouldSaveAuditLogWithCorrectData() {
        // Given
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("status", "COMPLETED");
        newValues.put("amount", 1500.50);

        Inet in = new Inet("192.168.1.1");

        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("orders")
                .recordId(999L)
                .actorIdentifier("admin@example.com")
                .newValues(newValues)
                .performedAt(LocalDateTime.now())
                .ipAddress(in)
                .build();

        // When
        AuditLogEntity saved = repository.save(auditLog);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNewValues()).containsEntry("status", "COMPLETED");
        assertThat(saved.getNewValues()).containsEntry("amount", 1500.50);
    }
}