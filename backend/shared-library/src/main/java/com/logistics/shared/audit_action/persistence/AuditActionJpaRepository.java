package com.logistics.shared.audit_action.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditActionJpaRepository extends JpaRepository<AuditActionTypeEntity, Integer> {

    Optional<AuditActionTypeEntity> findByCategory(String category);

    Optional<AuditActionTypeEntity> findByActionName(String actionName);
}
