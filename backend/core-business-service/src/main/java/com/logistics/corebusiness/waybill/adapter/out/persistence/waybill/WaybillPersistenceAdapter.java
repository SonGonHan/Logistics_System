package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Адаптер, реализующий интерфейс WaybillRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс WaybillRepository определяет контракт
 * - WaybillPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * <h2>Ответственность</h2>
 * - Преобразование Domain ↔ Entity через WaybillPersistenceMapper
 * - Делегирование операций в WaybillJpaRepository
 * - Маппинг результатов обратно в доменные объекты
 *
 * @implements WaybillRepository
 * @see WaybillRepository для контракта
 * @see WaybillJpaRepository для JPA работы
 * @see WaybillPersistenceMapper для преобразований
 */
@Component
@RequiredArgsConstructor
public class WaybillPersistenceAdapter implements WaybillRepository {

    private final WaybillJpaRepository jpaRepository;
    private final WaybillPersistenceMapper mapper;

    @Override
    public Waybill save(Waybill waybill) {
        WaybillEntity entity = mapper.toEntity(waybill);
        WaybillEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Waybill waybill) {
        WaybillEntity entity = mapper.toEntity(waybill);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Waybill> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Waybill> findByWaybillNumber(String waybillNumber) {
        return jpaRepository.findByWaybillNumber(waybillNumber).map(mapper::toDomain);
    }

    @Override
    public List<Waybill> findBySenderUserId(Long senderUserId) {
        return jpaRepository.findBySenderUserId(senderUserId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Waybill> findByRecipientUserId(Long recipientUserId) {
        return jpaRepository.findByRecipientUserId(recipientUserId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Waybill> findByStatus(WaybillStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Waybill> findByWaybillCreatorId(Long creatorId) {
        return jpaRepository.findByWaybillCreatorId(creatorId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
