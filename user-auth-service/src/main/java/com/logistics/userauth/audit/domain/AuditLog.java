package com.logistics.userauth.audit.domain;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.user.domain.User;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

    private long id;

    private User user;

    private AuditActionType actionType;

    private String tableName;

    private long recordId;

    private String actorIdentifier;

    private Map<String, Object> newValues;

    private LocalDateTime performedAt;

    private Inet ipAddress;
}
