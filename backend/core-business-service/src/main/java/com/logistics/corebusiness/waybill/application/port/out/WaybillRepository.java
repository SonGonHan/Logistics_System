package com.logistics.corebusiness.waybill.application.port.out;

import com.logistics.corebusiness.waybill.adapter.out.persistence.waybill.WaybillPersistenceAdapter;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;

import java.util.List;
import java.util.Optional;

/**
 * Порт (интерфейс) для работы с хранилищем накладных.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для всех операций с подтвержденными накладными,
 * не привязываясь к конкретной реализации (JPA, MongoDB и т.д.).
 *
 * <h2>Реализации</h2>
 * - WaybillPersistenceAdapter (текущая - JPA)
 * - Может быть заменена на другую реализацию при необходимости
 *
 * <h2>Методы</h2>
 * - save(waybill) - Сохранить или обновить накладную
 * - delete(waybill) - Удалить накладную (физическое удаление)
 * - findById(id) - Найти по ID
 * - findByWaybillNumber(number) - Найти по уникальному номеру накладной
 * - findBySenderUserId(id) - Все накладные отправителя
 * - findByRecipientUserId(id) - Все накладные получателя
 * - findByStatus(status) - Все накладные с определенным статусом
 * - findByWaybillCreatorId(id) - Все накладные, созданные определенным оператором
 *
 * @see WaybillPersistenceAdapter для реализации на JPA
 * @see Waybill для доменной сущности
 */
public interface WaybillRepository {

    Waybill save(Waybill waybill);

    void delete(Waybill waybill);

    Optional<Waybill> findById(Long id);

    Optional<Waybill> findByWaybillNumber(String waybillNumber);

    List<Waybill> findBySenderUserId(Long senderUserId);

    List<Waybill> findByRecipientUserId(Long recipientUserId);

    List<Waybill> findByStatus(WaybillStatus status);

    List<Waybill> findByWaybillCreatorId(Long creatorId);
}
