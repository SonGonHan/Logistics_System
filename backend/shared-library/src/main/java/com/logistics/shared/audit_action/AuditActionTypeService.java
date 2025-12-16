package com.logistics.shared.audit_action;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionJpaRepository;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Бизнес-сервис для работы с типами аудит-действий.
 *
 * <h2>Ответственность</h2>
 * Предоставляет различные способы поиска типов аудит-действий:
 * - По ID
 * - По имени действия (actionName)
 * - По категории
 *
 * @see AuditActionJpaRepository для работы с БД
 * @see AuditActionTypeMapper для преобразования Entity ↔ Domain
 */
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