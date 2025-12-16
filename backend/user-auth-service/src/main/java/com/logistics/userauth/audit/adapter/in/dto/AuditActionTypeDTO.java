package com.logistics.userauth.audit.adapter.in.dto;

import lombok.Builder;

/**
 * DTO для передачи информации о типе audit-лога.
 *
 * <h2>Назначение</h2>
 * Содержит публичную информацию о типе audit-лога.
 *
 * <h2>Примеры</h2>
 * {
 *   \"actionType\": \"USER_REGISTER\",
 *   \"category\": \"Authentication\",
 *   \"category\": \"Регистрация нового пользователя\"
 * }
 */
@Builder
public record AuditActionTypeDTO (String actionType, String category, String description) {

}
