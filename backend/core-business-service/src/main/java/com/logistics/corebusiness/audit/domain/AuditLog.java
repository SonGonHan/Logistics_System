package com.logistics.corebusiness.audit.domain;

import com.logistics.shared.audit_action.domain.AuditActionType;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Доменная сущность для логирования аудита.
 *
 * <h2>Назначение</h2>
 * Записывает все значимые действия в core-business-service:
 * - Работа с черновиками (DRAFT_CREATE, DRAFT_UPDATE, DRAFT_CANCEL)
 * - Приемка на ПВЗ (PVZ_ACCEPTANCE_START, PVZ_WEIGHT_VERIFY, PVZ_PHOTO_UPLOAD)
 * - Управление накладными (WAYBILL_FINALIZE, WAYBILL_CREATE, WAYBILL_STATUS_CHANGE, WAYBILL_CANCEL)
 * - Дополнительные услуги (WAYBILL_SERVICE_ADD)
 * - Рейтинг (RATING_SUBMIT)
 *
 * <h2>Отличия от user-auth-service</h2>
 * - userId хранится как Long, а не как объект User (этот сервис не владеет таблицей users)
 *
 * @see AuditActionType для типов действий
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

    private long id;
    private Long userId;
    private AuditActionType actionType;
    private String tableName;
    private long recordId;
    private String actorIdentifier;
    private Map<String, Object> newValues;
    private LocalDateTime performedAt;
    private Inet ipAddress;
}
