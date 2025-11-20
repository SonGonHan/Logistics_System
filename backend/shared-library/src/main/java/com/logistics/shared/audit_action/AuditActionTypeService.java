package com.logistics.shared.audit_action;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionJpaRepository;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditActionTypeService {

    private final AuditActionJpaRepository repo;
    private final AuditActionTypeMapper mapper;

    public Optional<AuditActionType> getActionTypeById(Integer id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    public Optional<AuditActionType> getActionTypeActionName(String actionName) {
        return repo.findByActionName(actionName).map(mapper::toDomain);
    }

    public Optional<AuditActionType> getActionTypeByCategory(String category) {
        return repo.findByCategory(category).map(mapper::toDomain);
    }

}