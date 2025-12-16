package com.logistics.userauth.audit.adapter.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.adapter.in.web.dto.UserDTO;
import com.logistics.userauth.user.adapter.out.persistence.UserPersistenceMapper;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между Domain AuditLog и Entity AuditLog.
 *
 * <h2>Назначение</h2>
 * Конвертирует Domain → Entity и обратно.
 *
 * @see AuditLog для доменной сущности
 * @see AuditLogEntity для сущности БД
 */
@Component
@RequiredArgsConstructor
public class AuditLogPersistenceMapper {

    private final AuditActionTypeMapper actionTypeMapper;
    private final UserPersistenceMapper userMapper;

    public AuditLogEntity toEntity(AuditLog domain) {
        return AuditLogEntity.builder()
                .id(domain.getId())
                .user(userMapper.toEntity(domain.getUser()))
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
                .user(userMapper.toDomain(entity.getUser()))
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
