package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA сущность для хранения истории изменений статусов накладных в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: waybill_service
 * Table: waybill_status_history
 *
 * Индексы: waybill_id, changed_at, facility_id
 *
 * <h2>Важные особенности</h2>
 * - Таблица только для добавления (append-only), удаления/изменения не предусмотрены
 * - facility_id и changedBy могут быть NULL (для системных изменений)
 * - notes может содержать произвольный текст (например, причину отмены)
 * - status маппится через @Enumerated(STRING) с CHECK constraint в БД
 *
 * @see WaybillStatusHistoryJpaRepository для работы с БД
 * @see WaybillStatusHistoryPersistenceMapper для преобразования Domain ↔ Entity
 * @see WaybillStatusHistory для доменной модели
 */
@Entity
@Table(
        name = "waybill_status_history",
        schema = "waybill_service",
        indexes = {
                @Index(columnList = "waybill_id", name = "idx_history_waybill_id"),
                @Index(columnList = "changed_at", name = "idx_history_changed_at"),
                @Index(columnList = "facility_id", name = "idx_history_facility_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaybillStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq")
    @SequenceGenerator(
            name = "history_seq",
            sequenceName = "waybill_status_history_history_id_seq",
            schema = "waybill_service",
            allocationSize = 1
    )
    @Column(name = "history_id")
    private Long id;

    @Column(name = "waybill_id", nullable = false)
    private Long waybillId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WaybillStatus status;

    @Column(name = "facility_id")
    private Long facilityId;

    @Column(name = "notes")
    private String notes;

    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}
