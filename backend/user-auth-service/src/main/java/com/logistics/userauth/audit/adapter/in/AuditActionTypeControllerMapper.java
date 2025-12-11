package com.logistics.userauth.audit.adapter.in;

import com.logistics.shared.audit_action.AuditActionTypeService;
import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.adapter.in.dto.AuditActionTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditActionTypeControllerMapper {

    private final AuditActionTypeService service;

    public AuditActionTypeDTO toDTO(AuditActionType domain) {
        return AuditActionTypeDTO.builder()
                .actionType(domain.getActionName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .build();
    }

    public AuditActionType toDomain(AuditActionTypeDTO dto) {
        return AuditActionType.builder()
                .id(service.getActionTypeActionName(dto.actionType()).get().getId())
                .actionName(dto.actionType())
                .category(dto.category())
                .description(dto.description())
                .build();
    }
}
