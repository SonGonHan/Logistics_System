package com.logistics.userauth.audit.adapter.in.dto;

import com.logistics.userauth.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record AuditLogDTO (
        User user,
        AuditActionTypeDTO actionTypeDTO,
        String tableName,
        long recordId,
        String actorIdentifier,
        Map<String, Object> newValues,
        LocalDateTime performedAt
) {
}
