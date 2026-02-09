package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import com.logistics.corebusiness.waybill.application.port.out.WaybillStatusHistoryRepository;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Адаптер, реализующий интерфейс WaybillStatusHistoryRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс WaybillStatusHistoryRepository определяет контракт
 * - WaybillStatusHistoryPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * <h2>Ответственность</h2>
 * - Преобразование Domain ↔ Entity через WaybillStatusHistoryPersistenceMapper
 * - Делегирование операций в WaybillStatusHistoryJpaRepository
 * - Маппинг результатов обратно в доменные объекты
 *
 * @implements WaybillStatusHistoryRepository
 * @see WaybillStatusHistoryRepository для контракта
 * @see WaybillStatusHistoryJpaRepository для JPA работы
 * @see WaybillStatusHistoryPersistenceMapper для преобразований
 */
@Component
@RequiredArgsConstructor
public class WaybillStatusHistoryPersistenceAdapter implements WaybillStatusHistoryRepository {

    private final WaybillStatusHistoryJpaRepository jpaRepository;
    private final WaybillStatusHistoryPersistenceMapper mapper;

    @Override
    public WaybillStatusHistory save(WaybillStatusHistory entry) {
        WaybillStatusHistoryEntity entity = mapper.toEntity(entry);
        WaybillStatusHistoryEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(WaybillStatusHistory entry) {
        WaybillStatusHistoryEntity entity = mapper.toEntity(entry);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<WaybillStatusHistory> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<WaybillStatusHistory> findByWaybillId(Long waybillId) {
        return jpaRepository.findByWaybillIdOrderByChangedAtAsc(waybillId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WaybillStatusHistory> findByFacilityId(Long facilityId) {
        return jpaRepository.findByFacilityId(facilityId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WaybillStatusHistory> findByChangedBy(Long userId) {
        return jpaRepository.findByChangedBy(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
