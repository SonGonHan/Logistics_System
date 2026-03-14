package com.logistics.corebusiness.audit.adapter.out.persistence;

import com.logistics.corebusiness.audit.domain.AuditLog;
import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
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
@DisplayName("AuditLogPersistenceMapper: юнит-тесты")
class AuditLogPersistenceMapperTest {

    @Mock
    private AuditActionTypeMapper actionTypeMapper;

    @InjectMocks
    private AuditLogPersistenceMapper mapper;

    private AuditActionType testActionType;
    private AuditActionTypeEntity testActionTypeEntity;

    @BeforeEach
    void setUp() {
        testActionType = AuditActionType.builder().id((short) 1).actionName("DRAFT_CREATE").build();
        testActionTypeEntity = AuditActionTypeEntity.builder().id((short) 1).actionName("DRAFT_CREATE").build();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("status", "created");
        var domain = AuditLog.builder()
                .id(1L)
                .userId(10L)
                .actionType(testActionType)
                .tableName("waybill_drafts")
                .recordId(100L)
                .newValues(newValues)
                .build();
        when(actionTypeMapper.toEntity(any(AuditActionType.class))).thenReturn(testActionTypeEntity);

        var entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getActionType()).isEqualTo(testActionTypeEntity);
        assertThat(entity.getTableName()).isEqualTo("waybill_drafts");
        assertThat(entity.getRecordId()).isEqualTo(100L);
        assertThat(entity.getNewValues()).containsEntry("status", "created");
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("status", "updated");
        var entity = AuditLogEntity.builder()
                .id(2L)
                .userId(20L)
                .actionType(testActionTypeEntity)
                .tableName("waybills")
                .recordId(200L)
                .newValues(newValues)
                .build();
        when(actionTypeMapper.toDomain(any(AuditActionTypeEntity.class))).thenReturn(testActionType);

        var domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getUserId()).isEqualTo(20L);
        assertThat(domain.getActionType()).isEqualTo(testActionType);
        assertThat(domain.getTableName()).isEqualTo("waybills");
        assertThat(domain.getRecordId()).isEqualTo(200L);
        assertThat(domain.getNewValues()).containsEntry("status", "updated");
    }
}
