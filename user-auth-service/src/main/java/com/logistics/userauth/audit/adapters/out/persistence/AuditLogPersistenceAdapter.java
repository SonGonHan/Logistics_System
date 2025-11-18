package com.logistics.userauth.audit.adapters.out.persistence;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.shared.audit_action.persistence.AuditActionTypeMapper;
import com.logistics.userauth.audit.app.out.AuditLogRepository;
import com.logistics.userauth.audit.domain.AuditLog;
import com.logistics.userauth.user.adapters.out.persistence.UserPersistenceMapper;
import com.logistics.userauth.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepo;
    private final AuditLogPersistenceMapper auditLogMapper;
    private final UserPersistenceMapper userMapper;
    private final AuditActionTypeMapper auditActionTypeMapper;

    @Override
    public void save(AuditLog auditLog) {
        AuditLogEntity auditLogEntity = auditLogMapper.toEntity(auditLog);
        jpaRepo.save(auditLogEntity);
    }

    @Override
    public void delete(AuditLog auditLog) {
        AuditLogEntity auditLogEntity = auditLogMapper.toEntity(auditLog);
        jpaRepo.delete(auditLogEntity);
    }

    @Override
    public List<AuditLog> findByUser(User user) {
        return jpaRepo.findByUser(userMapper.toEntity(user)).stream().map(auditLogMapper::toDomain).toList();
    }

    @Override
    public Optional<AuditLog> findByActionType(AuditActionType actionType) {
        return jpaRepo.findByActionType(auditActionTypeMapper.toEntity(actionType)).map(auditLogMapper::toDomain);
    }

    @Override
    public Optional<AuditLog> findByActorIdentifier(String actorIdentifier) {
        return jpaRepo.findByActorIdentifier(actorIdentifier).map(auditLogMapper::toDomain);
    }
}
