package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import com.logistics.corebusiness.waybill.application.port.out.WaybillStatusHistoryRepository;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistoryEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Адаптер для работы с репозиторием истории статусов накладных через JPA.
 */
@Component
@RequiredArgsConstructor
public class WaybillStatusHistoryPersistenceAdapter implements WaybillStatusHistoryRepository {

    private final WaybillStatusHistoryJpaRepository jpaRepository;
    private final WaybillStatusHistoryPersistenceMapper mapper;

    @Override
    public WaybillStatusHistoryEntry save(WaybillStatusHistoryEntry entry) {
        WaybillStatusHistoryEntity entity = mapper.toEntity(entry);
        WaybillStatusHistoryEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(WaybillStatusHistoryEntry entry) {
        WaybillStatusHistoryEntity entity = mapper.toEntity(entry);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<WaybillStatusHistoryEntry> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<WaybillStatusHistoryEntry> findByWaybillId(Long waybillId) {
        return jpaRepository.findByWaybillIdOrderByChangedAtAsc(waybillId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WaybillStatusHistoryEntry> findByFacilityId(Long facilityId) {
        return jpaRepository.findByFacilityId(facilityId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WaybillStatusHistoryEntry> findByChangedBy(Long userId) {
        return jpaRepository.findByChangedBy(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
