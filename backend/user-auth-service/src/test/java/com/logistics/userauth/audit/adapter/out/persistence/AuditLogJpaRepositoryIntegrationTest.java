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

    @Test
    @Transactional
    @DisplayName("Должен найти лог по типу действия")
    void shouldFindByActionType() {
        // Given
        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(Map.of("key", "value"))
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("192.168.1.1"))
                .build();

        repository.save(auditLog);

        // When
        var found = repository.findByActionType(testActionType);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getActionType().getActionName()).isEqualTo("TEST_ACTION");
        assertThat(found.get().getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске по несуществующему типу действия")
    void shouldReturnEmptyOptionalWhenActionTypeNotFound() {
        // Given
        AuditActionTypeEntity nonExistentActionType = AuditActionTypeEntity.builder()
                .actionName("NON_EXISTENT")
                .category("TEST")
                .description("Non existent")
                .build();
        nonExistentActionType = actionTypeRepository.save(nonExistentActionType);

        // When
        var found = repository.findByActionType(nonExistentActionType);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен найти лог по идентификатору актора")
    void shouldFindByActorIdentifier() {
        // Given
        String actorIdentifier = "actor@example.com";

        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier(actorIdentifier)
                .newValues(Map.of("key", "value"))
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("192.168.1.1"))
                .build();

        repository.save(auditLog);

        // When
        var found = repository.findByActorIdentifier(actorIdentifier);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getActorIdentifier()).isEqualTo(actorIdentifier);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске по несуществующему актору")
    void shouldReturnEmptyOptionalWhenActorIdentifierNotFound() {
        // When
        var found = repository.findByActorIdentifier("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой список для пользователя без логов")
    void shouldReturnEmptyListWhenUserHasNoLogs() {
        // Given
        UserEntity userWithoutLogs = UserEntity.builder()
                .phone("+79992222222")
                .firstName("Another")
                .lastName("User")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();
        userWithoutLogs = userRepository.save(userWithoutLogs);

        // When
        List<AuditLogEntity> found = repository.findByUser(userWithoutLogs);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть все логи для пользователя с несколькими записями")
    void shouldReturnAllLogsForUserWithMultipleEntries() {
        // Given
        AuditLogEntity log1 = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("table1")
                .recordId(1L)
                .actorIdentifier("actor1@example.com")
                .newValues(Map.of("action", "create"))
                .performedAt(LocalDateTime.now().minusHours(2))
                .ipAddress(new Inet("192.168.1.1"))
                .build();

        AuditLogEntity log2 = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("table2")
                .recordId(2L)
                .actorIdentifier("actor2@example.com")
                .newValues(Map.of("action", "update"))
                .performedAt(LocalDateTime.now().minusHours(1))
                .ipAddress(new Inet("192.168.1.2"))
                .build();

        AuditLogEntity log3 = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("table3")
                .recordId(3L)
                .actorIdentifier("actor3@example.com")
                .newValues(Map.of("action", "delete"))
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("192.168.1.3"))
                .build();

        repository.saveAll(List.of(log1, log2, log3));

        // When
        List<AuditLogEntity> found = repository.findByUser(testUser);

        // Then
        assertThat(found).hasSize(3);
        assertThat(found).extracting(AuditLogEntity::getTableName)
                .containsExactlyInAnyOrder("table1", "table2", "table3");
    }

    @Test
    @DisplayName("Должен корректно сохранять IPv6 адрес")
    void shouldSaveIpv6Address() {
        // Given
        Inet ipv6 = new Inet("2001:0db8:85a3:0000:0000:8a2e:0370:7334");

        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(Map.of("key", "value"))
                .performedAt(LocalDateTime.now())
                .ipAddress(ipv6)
                .build();

        // When
        AuditLogEntity saved = repository.save(auditLog);

        // Then
        assertThat(saved.getIpAddress()).isEqualTo(ipv6);
    }

    @Test
    @DisplayName("Должен корректно сохранять сложные вложенные структуры в JSONB")
    void shouldSaveComplexJsonbStructure() {
        // Given
        Map<String, Object> complexValues = new HashMap<>();
        complexValues.put("simpleString", "value");
        complexValues.put("number", 42);
        complexValues.put("decimal", 99.99);
        complexValues.put("boolean", true);
        complexValues.put("nested", Map.of(
                "innerKey", "innerValue",
                "innerNumber", 123
        ));
        complexValues.put("array", List.of("item1", "item2", "item3"));

        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(complexValues)
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("192.168.1.1"))
                .build();

        // When
        AuditLogEntity saved = repository.save(auditLog);
        repository.flush();
        AuditLogEntity reloaded = repository.findById(saved.getId()).orElseThrow();

        // Then
        assertThat(reloaded.getNewValues()).containsEntry("simpleString", "value");
        assertThat(reloaded.getNewValues()).containsEntry("number", 42);
        assertThat(reloaded.getNewValues()).containsEntry("boolean", true);
        assertThat(reloaded.getNewValues()).containsKey("nested");
        assertThat(reloaded.getNewValues()).containsKey("array");
    }

    @Test
    @DisplayName("Должен сохранить лог без пользователя (null user)")
    void shouldSaveAuditLogWithoutUser() {
        // Given
        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(null)
                .actionType(testActionType)
                .tableName("system_table")
                .recordId(1L)
                .actorIdentifier("system")
                .newValues(Map.of("action", "system_action"))
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("127.0.0.1"))
                .build();

        // When
        AuditLogEntity saved = repository.save(auditLog);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser()).isNull();
        assertThat(saved.getActorIdentifier()).isEqualTo("system");
    }

    @Test
    @DisplayName("Должен сохранить лог без IP-адреса (null ipAddress)")
    void shouldSaveAuditLogWithoutIpAddress() {
        // Given
        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(Map.of("key", "value"))
                .performedAt(LocalDateTime.now())
                .ipAddress(null)
                .build();

        // When
        AuditLogEntity saved = repository.save(auditLog);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getIpAddress()).isNull();
    }

    @Test
    @DisplayName("Должен сохранить лог с пустыми newValues")
    void shouldSaveAuditLogWithEmptyNewValues() {
        // Given
        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(Map.of())
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("192.168.1.1"))
                .build();

        // When
        AuditLogEntity saved = repository.save(auditLog);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNewValues()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен корректно работать Lazy Loading для связи с User")
    void shouldLazyLoadUserRelation() {
        // Given
        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(Map.of("key", "value"))
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("192.168.1.1"))
                .build();

        AuditLogEntity saved = repository.save(auditLog);
        repository.flush();

        // When - загружаем сущность и проверяем lazy loading
        AuditLogEntity reloaded = repository.findById(saved.getId()).orElseThrow();

        // Then
        assertThat(reloaded.getUser()).isNotNull();
        assertThat(reloaded.getUser().getPhone()).isEqualTo("+79991111111");
    }

    @Test
    @Transactional
    @DisplayName("Должен корректно работать Lazy Loading для связи с ActionType")
    void shouldLazyLoadActionTypeRelation() {
        // Given
        AuditLogEntity auditLog = AuditLogEntity.builder()
                .user(testUser)
                .actionType(testActionType)
                .tableName("test_table")
                .recordId(1L)
                .actorIdentifier("test@example.com")
                .newValues(Map.of("key", "value"))
                .performedAt(LocalDateTime.now())
                .ipAddress(new Inet("192.168.1.1"))
                .build();

        AuditLogEntity saved = repository.save(auditLog);
        repository.flush();

        // When - загружаем сущность и проверяем lazy loading
        AuditLogEntity reloaded = repository.findById(saved.getId()).orElseThrow();

        // Then
        assertThat(reloaded.getActionType()).isNotNull();
        assertThat(reloaded.getActionType().getActionName()).isEqualTo("TEST_ACTION");
    }
}