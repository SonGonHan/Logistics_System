package com.logistics.corebusiness.audit.adapter.out.persistence;

import com.logistics.corebusiness.audit.domain.AuditLog;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между Domain AuditLog и Entity AuditLogEntity.
 *
 * <h2>Назначение</h2>
 * Конвертирует Domain -> Entity и обратно.
 *
 * <h2>Отличия от user-auth-service</h2>
 * - Не использует UserPersistenceMapper (userId хранится как Long)
 *
 * @see AuditLog для доменной сущности
 * @see AuditLogEntity для сущности БД
 */
@Component
@RequiredArgsConstructor
public class AuditLogPersistenceMapper {

    private final AuditActionTypeMapper actionTypeMapper;

    public AuditLogEntity toEntity(AuditLog domain) {
        return AuditLogEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .actionType(actionTypeMapper.toEntity(domain.getActionType()))
                .actorIdentifier(domain.getActorIdentifier())
                .ipAddress(domain.getIpAddress())
                .newValues(domain.getNewValues())
                .performedAt(domain.getPerformedAt())
                .tableName(domain.getTableName())
                .recordId(domain.getRecordId())
                .build();
    }

    public AuditLog toDomain(AuditLogEntity entity) {
        return AuditLog.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .actionType(actionTypeMapper.toDomain(entity.getActionType()))
                .actorIdentifier(entity.getActorIdentifier())
                .ipAddress(entity.getIpAddress())
                .newValues(entity.getNewValues())
                .performedAt(entity.getPerformedAt())
                .tableName(entity.getTableName())
                .recordId(entity.getRecordId())
                .build();
    }
}
