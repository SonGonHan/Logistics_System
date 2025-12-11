package com.logistics.userauth.audit.adapter.out.persistence;

import com.logistics.shared.audit_action.persistence.AuditActionTypeEntity;
import com.logistics.userauth.user.adapter.out.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findByUser(UserEntity user);

    Optional<AuditLogEntity> findByActionType(AuditActionTypeEntity actionType);

    Optional<AuditLogEntity> findByActorIdentifier(String actorIdentifier);

}
