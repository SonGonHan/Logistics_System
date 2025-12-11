package com.logistics.userauth.audit.adapter.in.dto;

import lombok.Builder;

@Builder
public record AuditActionTypeDTO (String actionType, String category, String description) {

}
