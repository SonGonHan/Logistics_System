package com.logistics.userauth.audit.adapters.in.dto;

import com.logistics.userauth.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@Builder
public class AuditLogDTO {

    private User user;

    private AuditActionTypeDTO actionTypeDTO;

    private String tableName;

    private long recordId;

    private String actorIdentifier;

    private Map<String, Object> newValues;

    private LocalDateTime performedAt;
}
