package com.logistics.corebusiness.waybill.application.port.out;

import com.logistics.corebusiness.waybill.adapter.out.persistence.draft.WaybillDraftPersistenceAdapter;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.WaybillDraft;

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
 * - WaybillDraftPersistenceAdapter (текущая - JPA)
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
 * @see WaybillDraftPersistenceAdapter для реализации на JPA
 * @see WaybillDraft для доменной сущности
 */
public interface WaybillDraftRepository {

    WaybillDraft save(WaybillDraft draft);

    void delete(WaybillDraft draft);

    Optional<WaybillDraft> findById(Long id);

    Optional<WaybillDraft> findByBarcode(String barcode);

    List<WaybillDraft> findBySenderUserId(Long senderUserId);

    List<WaybillDraft> findByRecipientUserId(Long recipientUserId);

    List<WaybillDraft> findByDraftStatus(DraftStatus status);

    List<WaybillDraft> findByDraftCreatorId(Long creatorId);
}
