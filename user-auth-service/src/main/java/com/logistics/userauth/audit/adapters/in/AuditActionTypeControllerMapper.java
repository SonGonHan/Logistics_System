package com.logistics.userauth.audit.adapters.in;

import com.logistics.shared.audit_action.AuditActionTypeService;
import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.adapters.in.dto.AuditActionTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditActionTypeControllerMapper {

    private final AuditActionTypeService service;

    public AuditActionTypeDTO toDTO(AuditActionType domain) {
        return AuditActionTypeDTO.builder()
                .actionName(domain.getActionName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .build();
    }

    public AuditActionType toDomain(AuditActionTypeDTO dto) {
        return AuditActionType.builder()
                .id(service.getActionTypeActionName(dto.getActionName()).get().getId())
                .actionName(dto.getActionName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .build();
    }
}
