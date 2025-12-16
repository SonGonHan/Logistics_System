package com.logistics.userauth.audit.adapter.in.dto;

import com.logistics.userauth.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO для передачи информации о audit-логах.
 *
 * <h2>Назначение</h2>
 * Содержит публичную информацию о важных логах для отправки клиенту.
 *
 * <h2>Примеры</h2>
 * {
 *   \"user\": \"1\",
 *   \"actionTypeDTO\": \"1\",
 *   \"tableName\": \"\",
 *   \"recordId\": \"\",
 *   \"actorIdentifier\": \"+71234567890\",
 *   \"newValues\": \"\",
 *   \"performedAt\": \"16.12.2025 16:20:36\",
 * }
 */
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
