package com.logistics.userauth.audit.adapters.in;

import com.logistics.userauth.audit.adapters.in.dto.AuditActionTypeDTO;
import com.logistics.userauth.audit.adapters.in.dto.AuditLogDTO;
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
                .user(dto.getUser())
                .actionType(actionTypeMapper.toDomain(dto.getActionTypeDTO()))
                .tableName(dto.getTableName())
                .recordId(dto.getRecordId())
                .actorIdentifier(dto.getActorIdentifier())
                .performedAt(dto.getPerformedAt())
                .newValues(dto.getNewValues())
                .build();
    }
}
