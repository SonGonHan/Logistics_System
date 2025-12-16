// backend/com/logistics/shared/audit_action/package-info.java
/**
 * Пакет управления типами аудиторских действий.
 *
 * Предоставляет сервис для получения типизированных описаний действий пользователей
 * (например, "USER_LOGIN", "ORDER_CREATED" и т.д.).
 *
 * Используется в других микросервисах для логирования:
 * <pre>{@code
 * @Component
 * public class OrderService {
 *     private final AuditActionTypeService auditService;
 *
 *     public void createOrder(...) {
 *         // Создаём заказ
 *         // ...
 *
 *         // Логируем действие
 *         var actionType = auditService.getActionTypeActionName("ORDER_CREATED");
 *         auditLog.save(AuditLog.builder()
 *             .actionType(actionType.get())
 *             .user(user)
 *             .build());
 *     }
 * }
 * }</pre>
 */
package com.logistics.shared.audit_action;