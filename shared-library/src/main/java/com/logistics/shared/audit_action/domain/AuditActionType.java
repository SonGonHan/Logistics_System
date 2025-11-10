package com.logistics.shared.audit_action.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditActionType {

    private short id;

    private String actionName;

    private String category;

    private String description;
}
