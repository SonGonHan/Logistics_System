package com.logistics.userauth.audit.adapter.in;

import com.logistics.userauth.audit.adapter.in.dto.AuditLogDTO;
import com.logistics.userauth.audit.domain.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogControllerMapper {

    private final AuditActionTypeControllerMapper actionTypeMapper;

    public AuditLogDTO toDTO(AuditLog domain) {
        return AuditLogDTO.builder()
                .user(domain.getUser())
                .actionTypeDTO(actionTypeMapper.toDTO(domain.getActionType()))
                .tableName(domain.getTableName())
                .recordId(domain.getRecordId())
                .actorIdentifier(domain.getActorIdentifier())
                .performedAt(domain.getPerformedAt())
                .newValues(domain.getNewValues())
                .build();
    }

    public AuditLog toDomain(AuditLogDTO dto) {
        return AuditLog.builder()
                .user(dto.user())
                .actionType(actionTypeMapper.toDomain(dto.actionTypeDTO()))
                .tableName(dto.tableName())
                .recordId(dto.recordId())
                .actorIdentifier(dto.actorIdentifier())
                .performedAt(dto.performedAt())
                .newValues(dto.newValues())
                .build();
    }
}
