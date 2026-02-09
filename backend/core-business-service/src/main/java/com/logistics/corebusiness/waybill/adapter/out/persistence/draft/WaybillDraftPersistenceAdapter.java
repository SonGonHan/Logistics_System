package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.application.port.out.WaybillDraftRepository;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.WaybillDraft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Адаптер, реализующий интерфейс WaybillDraftRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс WaybillDraftRepository определяет контракт
 * - WaybillDraftPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * <h2>Ответственность</h2>
 * - Преобразование Domain ↔ Entity через WaybillDraftPersistenceMapper
 * - Делегирование операций в WaybillDraftJpaRepository
 * - Маппинг результатов обратно в доменные объекты
 *
 * @implements WaybillDraftRepository
 * @see WaybillDraftRepository для контракта
 * @see WaybillDraftJpaRepository для JPA работы
 * @see WaybillDraftPersistenceMapper для преобразований
 */
@Component
@RequiredArgsConstructor
public class WaybillDraftPersistenceAdapter implements WaybillDraftRepository {

    private final WaybillDraftJpaRepository jpaRepository;
    private final WaybillDraftPersistenceMapper mapper;

    @Override
    public WaybillDraft save(WaybillDraft draft) {
        WaybillDraftEntity entity = mapper.toEntity(draft);
        WaybillDraftEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(WaybillDraft draft) {
        WaybillDraftEntity entity = mapper.toEntity(draft);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<WaybillDraft> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<WaybillDraft> findByBarcode(String barcode) {
        return jpaRepository.findByBarcode(barcode).map(mapper::toDomain);
    }

    @Override
    public List<WaybillDraft> findBySenderUserId(Long senderUserId) {
        return jpaRepository.findBySenderUserId(senderUserId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WaybillDraft> findByRecipientUserId(Long recipientUserId) {
        return jpaRepository.findByRecipientUserId(recipientUserId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WaybillDraft> findByDraftStatus(DraftStatus status) {
        return jpaRepository.findByDraftStatus(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WaybillDraft> findByDraftCreatorId(Long creatorId) {
        return jpaRepository.findByDraftCreatorId(creatorId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
