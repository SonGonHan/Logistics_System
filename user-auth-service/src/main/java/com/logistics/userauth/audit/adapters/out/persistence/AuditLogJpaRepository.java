package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.user.adapters.out.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {

    Optional<AuditLogEntity> findByUser(UserEntity user);

    Optional<AuditLogEntity> findByActionType(AuditActionTypeEntity actionType);

    Optional<AuditLogEntity> findByActorIdentifier(String actorIdentifier);

}
