package com.logistics.userauth.audit.application.port.in.command;

import java.util.Map;

/**
 * Команда для создания записи в журнале аудита.
 *
 * <h2>Назначение</h2>
 * Используется для передачи данных в {@link com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase}
 * при логировании действий пользователей в системе.
 *
 * <h2>Поля</h2>
 * - userId: ID пользователя (может быть null для неудачных попыток входа)
 * - actionTypeName: Имя типа действия (например, "USER_LOGIN_SUCCESS")
 * - actorIdentifier: Идентификатор актора (email или телефон)
 * - ipAddress: IP-адрес клиента (из HTTP request)
 * - userAgent: User-Agent браузера (опционально)
 * - newValues: JSONB данные с новыми значениями (для UPDATE операций)
 * - tableName: Имя таблицы, которая была изменена (опционально)
 * - recordId: ID записи в таблице (опционально)
 *
 * <h2>Примеры</h2>
 * <pre>
 * // Регистрация пользователя
 * new CreateAuditLogCommand(
 *     savedUser.getId(),
 *     "USER_REGISTER",
 *     savedUser.getPhone(),
 *     "192.168.1.1",
 *     "Mozilla/5.0...",
 *     Map.of("email", "user@example.com", "phone", "+79001234567", "role", "CLIENT"),
 *     "users",
 *     savedUser.getId()
 * )
 *
 * // Неудачная попытка входа
 * new CreateAuditLogCommand(
 *     null, // Неизвестный пользователь
 *     "USER_LOGIN_FAILURE",
 *     "+79001234567",
 *     "192.168.1.1",
 *     "Mozilla/5.0...",
 *     Map.of("attemptedPhone", "+79001234567", "reason", "INVALID_CREDENTIALS"),
 *     null,
 *     null
 * )
 * </pre>
 *
 * @param userId ID пользователя (может быть null для failed login)
 * @param actionTypeName Имя типа действия из shared_data.audit_action_types
 * @param actorIdentifier Email или телефон пользователя
 * @param ipAddress IP-адрес клиента
 * @param userAgent User-Agent браузера (опционально)
 * @param newValues JSONB данные с новыми значениями
 * @param tableName Имя таблицы (опционально, например "users")
 * @param recordId ID затронутой записи (опционально)
 */
public record CreateAuditLogCommand(
        Long userId,
        String actionTypeName,
        String actorIdentifier,
        String ipAddress,
        String userAgent,
        Map<String, Object> newValues,
        String tableName,
        Long recordId
) {
}