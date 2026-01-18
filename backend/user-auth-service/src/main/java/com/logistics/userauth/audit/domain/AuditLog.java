package com.logistics.userauth.audit.domain;

import com.logistics.shared.audit_action.domain.AuditActionType;
import com.logistics.userauth.audit.adapter.out.persistence.AuditLogEntity;
import com.logistics.userauth.user.domain.User;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Доменная сущность для логирования аудита.
 *
 * <h2>Назначение</h2>
 * Записывает все значимые действия пользователей в системе:
 * - Вход/выход (USER_LOGIN, USER_LOGOUT)
 * - Изменение профиля (PROFILE_UPDATED)
 * - Создание заказов (ORDER_CREATED)
 * - И другие действия, определенные в AuditActionType
 *
 * <h2>Структура</h2>
 * - userId: Уникальный идентификатор записи в логе
 * - user: Пользователь, совершивший действие
 * - actionType: Тип действия (ссылка на AuditActionType)
 * - tableName: Таблица, которая была изменена (если применимо)
 * - recordId: ID записи в таблице, которая была изменена
 * - actorIdentifier: Строка для идентификации актора (обычно email/телефон)
 * - newValues: JSONB с новыми значениями (для UPDATE операций)
 * - performedAt: Когда было выполнено действие
 * - ipAddress: IP-адрес клиента для аудита безопасности
 *
 * <h2>Примеры</h2>
 * <pre>
 * AuditLog log = AuditLog.builder()
 *   .user(user)
 *   .actionType(auditActionType)  // USER_LOGIN
 *   .actorIdentifier(\"john@example.com\")
 *   .performedAt(LocalDateTime.now())
 *   .ipAddress(new Inet(\"192.168.1.100\"))
 *   .build();
 * </pre>
 *
 * @see AuditActionType для типов действий
 * @see AuditLogEntity для JPA entity
 */
@Getter
@Setter
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
