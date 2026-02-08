package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import com.logistics.corebusiness.waybill.domain.WaybillStatusHistoryEntry;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между WaybillStatusHistoryEntry (domain) и WaybillStatusHistoryEntity (JPA).
 */
@Component
public class WaybillStatusHistoryPersistenceMapper {

    public WaybillStatusHistoryEntity toEntity(WaybillStatusHistoryEntry domain) {
        return WaybillStatusHistoryEntity.builder()
                .id(domain.getId())
                .waybillId(domain.getWaybillId())
                .status(domain.getStatus())
                .facilityId(domain.getFacilityId())
                .notes(domain.getNotes())
                .changedBy(domain.getChangedBy())
                .changedAt(domain.getChangedAt())
                .build();
    }

    public WaybillStatusHistoryEntry toDomain(WaybillStatusHistoryEntity entity) {
        return WaybillStatusHistoryEntry.builder()
                .id(entity.getId())
                .waybillId(entity.getWaybillId())
                .status(entity.getStatus())
                .facilityId(entity.getFacilityId())
                .notes(entity.getNotes())
                .changedBy(entity.getChangedBy())
                .changedAt(entity.getChangedAt())
                .build();
    }
}
