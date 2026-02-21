package com.logistics.corebusiness.waybill.application.port.out;

import com.logistics.corebusiness.waybill.adapter.out.persistence.history.WaybillStatusHistoryPersistenceAdapter;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;

import java.util.List;
import java.util.Optional;

/**
 * Порт (интерфейс) для работы с хранилищем истории статусов накладных.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для операций с историей изменений статусов,
 * не привязываясь к конкретной реализации (JPA, MongoDB и т.д.).
 *
 * <h2>Реализации</h2>
 * - WaybillStatusHistoryPersistenceAdapter (текущая - JPA)
 * - Может быть заменена на другую реализацию при необходимости
 *
 * <h2>Методы</h2>
 * - save(entry) - Сохранить новую запись в истории
 * - delete(entry) - Удалить запись (обычно не используется, append-only таблица)
 * - findById(id) - Найти запись по ID
 * - findByWaybillId(id) - Все записи истории для конкретной накладной
 * - findByFacilityId(id) - Все изменения на конкретном объекте (склад, ПВЗ)
 * - findByChangedBy(userId) - Все изменения, сделанные конкретным пользователем
 *
 * @see WaybillStatusHistoryPersistenceAdapter для реализации на JPA
 * @see WaybillStatusHistory для доменной сущности
 */
public interface WaybillStatusHistoryRepository {

    WaybillStatusHistory save(WaybillStatusHistory entry);

    void delete(WaybillStatusHistory entry);

    Optional<WaybillStatusHistory> findById(Long id);

    List<WaybillStatusHistory> findByWaybillId(Long waybillId);

    List<WaybillStatusHistory> findByFacilityId(Long facilityId);

    List<WaybillStatusHistory> findByChangedBy(Long userId);
}
