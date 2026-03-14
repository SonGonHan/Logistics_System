package com.logistics.corebusiness.audit.application.port.in.command;

import java.util.Map;

/**
 * Команда для создания записи в журнале аудита core-business-service.
 *
 * <h2>Примеры</h2>
 * <pre>
 * // Создание черновика
 * new CreateAuditLogCommand(
 *     userId,
 *     "DRAFT_CREATE",
 *     "+79001234567",
 *     "192.168.1.1",
 *     Map.of("barcode", "DRF-260209-123456", "pricingRuleId", 5L),
 *     "waybill_drafts",
 *     draftId
 * )
 *
 * // Приёмка на ПВЗ (начало)
 * new CreateAuditLogCommand(
 *     operatorId,
 *     "PVZ_ACCEPTANCE_START",
 *     "operator@pvz.ru",
 *     "10.0.0.1",
 *     Map.of("barcode", barcode, "draftId", draftId),
 *     "waybill_drafts",
 *     draftId
 * )
 * </pre>
 *
 * @param userId ID пользователя (может быть null)
 * @param actionTypeName Имя типа действия из shared_data.audit_action_types
 * @param actorIdentifier Идентификатор актора (телефон или email)
 * @param ipAddress IP-адрес клиента
 * @param newValues JSONB данные с деталями операции
 * @param tableName Имя таблицы (например "waybill_drafts", "waybills")
 * @param recordId ID затронутой записи
 */
public record CreateAuditLogCommand(
        Long userId,
        String actionTypeName,
        String actorIdentifier,
        String ipAddress,
        Map<String, Object> newValues,
        String tableName,
        Long recordId
) {
}
