package com.logistics.corebusiness.waybill.application.port.out;

import com.logistics.corebusiness.waybill.domain.WaybillStatusHistoryEntry;

import java.util.List;
import java.util.Optional;

/**
 * Порт для работы с репозиторием истории статусов накладных.
 */
public interface WaybillStatusHistoryRepository {

    WaybillStatusHistoryEntry save(WaybillStatusHistoryEntry entry);

    void delete(WaybillStatusHistoryEntry entry);

    Optional<WaybillStatusHistoryEntry> findById(Long id);

    List<WaybillStatusHistoryEntry> findByWaybillId(Long waybillId);

    List<WaybillStatusHistoryEntry> findByFacilityId(Long facilityId);

    List<WaybillStatusHistoryEntry> findByChangedBy(Long userId);
}
