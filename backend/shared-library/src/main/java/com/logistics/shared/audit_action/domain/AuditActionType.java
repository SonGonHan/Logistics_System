package com.logistics.shared.audit_action.domain;

import lombok.*;

/**
 * Доменная сущность для типа аудит-действия.
 *
 * <h2>Назначение</h2>
 * Описывает категорию действия, которое можно залогировать в системе:
 * - USER_LOGIN (категория: AUTHENTICATION)
 * - ORDER_CREATED (категория: ORDER_MANAGEMENT)
 * - PROFILE_UPDATED (категория: USER_MANAGEMENT)
 *
 * <h2>Структура</h2>
 * - id: короткий уникальный идентификатор типа
 * - actionName: машиночитаемое имя (USER_LOGIN, ORDER_CREATED)
 * - category: категория для группировки (AUTHENTICATION, ORDER_MANAGEMENT)
 * - description: читаемое описание на русском
 *
 * <h2>Примеры</h2>
 * <pre>
 * AuditActionType userLogin = AuditActionType.builder()
 *   .id((short) 1)
 *   .actionName(\"USER_LOGIN\")
 *   .category(\"AUTHENTICATION\")
 *   .description(\"Пользователь вошел в систему\")
 *   .build();
 * </pre>
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditActionType {

    private short id;

    private String actionName;

    private String category;

    private String description;
}
