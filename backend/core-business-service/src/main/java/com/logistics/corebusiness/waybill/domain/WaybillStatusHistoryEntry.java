package com.logistics.corebusiness.waybill.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Доменная сущность записи в истории изменений статуса накладной.
 * Используется для аудита и отслеживания движения посылки.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaybillStatusHistoryEntry {

    private Long id;
    private Long waybillId;
    private WaybillStatus status;
    private Long facilityId;
    private String notes;
    private Long changedBy;
    private LocalDateTime changedAt;
}
