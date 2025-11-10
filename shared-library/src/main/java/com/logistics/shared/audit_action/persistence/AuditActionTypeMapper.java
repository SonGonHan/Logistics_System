package com.logistics.shared.audit_action.persistence;

import com.logistics.shared.audit_action.domain.AuditActionType;
import org.springframework.stereotype.Component;

@Component
public class AuditActionTypeMapper {

    public AuditActionType toDomain(AuditActionTypeEntity entity) {
        return AuditActionType.builder()
                .id(entity.getId())
                .actionName(entity.getActionName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .build();
    }

    public AuditActionTypeEntity toEntity(AuditActionType domain) {
        return AuditActionTypeEntity.builder()
                .id(domain.getId())
                .actionName(domain.getActionName())
                .category(domain.getCategory())
                .description(domain.getDescription())
                .build();
    }
}
