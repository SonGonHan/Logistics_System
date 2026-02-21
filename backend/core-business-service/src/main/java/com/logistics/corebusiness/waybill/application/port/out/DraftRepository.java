package com.logistics.corebusiness.waybill.application.port.out;

import com.logistics.corebusiness.waybill.adapter.out.persistence.draft.DraftPersistenceAdapter;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.Draft;

import java.util.List;
import java.util.Optional;

/**
 * Порт (интерфейс) для работы с хранилищем черновиков накладных.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для всех операций с черновиками накладных,
 * не привязываясь к конкретной реализации (JPA, MongoDB и т.д.).
 *
 * <h2>Реализации</h2>
 * - DraftPersistenceAdapter (текущая - JPA)
 * - Может быть заменена на другую реализацию при необходимости
 *
 * <h2>Методы</h2>
 * - save(draft) - Сохранить или обновить черновик
 * - delete(draft) - Удалить черновик (физическое удаление)
 * - findById(id) - Найти по ID
 * - findByBarcode(barcode) - Найти по уникальному штрих-коду
 * - findBySenderUserId(id) - Все черновики отправителя
 * - findByRecipientUserId(id) - Все черновики для получателя
 * - findByDraftStatus(status) - Все черновики с определенным статусом
 * - findByDraftCreatorId(id) - Все черновики, созданные определенным пользователем
 *
 * @see DraftPersistenceAdapter для реализации на JPA
 * @see Draft для доменной сущности
 */
public interface DraftRepository {

    Draft save(Draft draft);

    void delete(Draft draft);

    Optional<Draft> findById(Long id);

    Optional<Draft> findByBarcode(String barcode);

    List<Draft> findBySenderUserId(Long senderUserId);

    List<Draft> findByRecipientUserId(Long recipientUserId);

    List<Draft> findByDraftStatus(DraftStatus status);

    List<Draft> findByDraftCreatorId(Long creatorId);
}
