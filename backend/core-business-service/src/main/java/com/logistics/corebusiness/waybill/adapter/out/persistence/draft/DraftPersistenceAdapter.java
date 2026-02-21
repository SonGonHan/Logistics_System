package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Адаптер, реализующий интерфейс DraftRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс DraftRepository определяет контракт
 * - DraftPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * <h2>Ответственность</h2>
 * - Преобразование Domain ↔ Entity через DraftPersistenceMapper
 * - Делегирование операций в DraftJpaRepository
 * - Маппинг результатов обратно в доменные объекты
 *
 * @implements DraftRepository
 * @see DraftRepository для контракта
 * @see DraftJpaRepository для JPA работы
 * @see DraftPersistenceMapper для преобразований
 */
@Component
@RequiredArgsConstructor
public class DraftPersistenceAdapter implements DraftRepository {

    private final DraftJpaRepository jpaRepository;
    private final DraftPersistenceMapper mapper;

    @Override
    public Draft save(Draft draft) {
        DraftEntity entity = mapper.toEntity(draft);
        DraftEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Draft draft) {
        DraftEntity entity = mapper.toEntity(draft);
        jpaRepository.delete(entity);
    }

    @Override
    public Optional<Draft> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Draft> findByBarcode(String barcode) {
        return jpaRepository.findByBarcode(barcode).map(mapper::toDomain);
    }

    @Override
    public List<Draft> findBySenderUserId(Long senderUserId) {
        return jpaRepository.findBySenderUserId(senderUserId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Draft> findByRecipientUserId(Long recipientUserId) {
        return jpaRepository.findByRecipientUserId(recipientUserId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Draft> findByDraftStatus(DraftStatus status) {
        return jpaRepository.findByDraftStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Draft> findByDraftCreatorId(Long creatorId) {
        return jpaRepository.findByDraftCreatorId(creatorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}