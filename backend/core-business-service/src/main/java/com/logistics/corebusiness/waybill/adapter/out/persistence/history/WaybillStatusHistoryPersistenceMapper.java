package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между доменной моделью и JPA сущностью истории.
 *
 * <h2>Назначение</h2>
 * Обеспечивает изоляцию доменного слоя от деталей JPA:
 * - Доменная модель (WaybillStatusHistory) не знает о JPA аннотациях
 * - JPA сущность (WaybillStatusHistoryEntity) не попадает в бизнес-логику
 *
 * <h2>Методы</h2>
 * - toEntity(WaybillStatusHistory) - Преобразование Domain → Entity (для сохранения в БД)
 * - toDomain(WaybillStatusHistoryEntity) - Преобразование Entity → Domain (для чтения из БД)
 *
 * <h2>Особенности</h2>
 * - Все enum-ы (WaybillStatus) копируются без преобразований
 * - LocalDateTime копируется по значению (immutable)
 * - Все Long ID копируются напрямую
 *
 * @see WaybillStatusHistory для доменной модели
 * @see WaybillStatusHistoryEntity для JPA сущности
 */
@Component
public class WaybillStatusHistoryPersistenceMapper {

    public WaybillStatusHistoryEntity toEntity(WaybillStatusHistory domain) {
        return WaybillStatusHistoryEntity.builder()
                .id(domain.getId())
                .waybillId(domain.getWaybillId())
                .status(domain.getStatus())
                .facilityId(domain.getFacilityId())
                .notes(domain.getNotes())
                .changedBy(domain.getChangedBy())
                .changedAt(domain.getChangedAt())
                .build();
    }

    public WaybillStatusHistory toDomain(WaybillStatusHistoryEntity entity) {
        return WaybillStatusHistory.builder()
                .id(entity.getId())
                .waybillId(entity.getWaybillId())
                .status(entity.getStatus())
                .facilityId(entity.getFacilityId())
                .notes(entity.getNotes())
                .changedBy(entity.getChangedBy())
                .changedAt(entity.getChangedAt())
                .build();
    }
}
