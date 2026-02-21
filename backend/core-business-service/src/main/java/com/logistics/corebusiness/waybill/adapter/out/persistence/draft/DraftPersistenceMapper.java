package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.domain.Draft;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между доменной моделью и JPA сущностью черновика.
 *
 * <h2>Назначение</h2>
 * Обеспечивает изоляцию доменного слоя от деталей JPA:
 * - Доменная модель (Draft) не знает о JPA аннотациях
 * - JPA сущность (DraftEntity) не попадает в бизнес-логику
 *
 * <h2>Методы</h2>
 * - toEntity(Draft) - Преобразование Domain → Entity (для сохранения в БД)
 * - toDomain(DraftEntity) - Преобразование Entity → Domain (для чтения из БД)
 *
 * <h2>Особенности</h2>
 * - Dimensions (Value Object) копируется напрямую (record)
 * - Все enum-ы (DraftStatus) копируются без преобразований
 * - BigDecimal копируются по значению (immutable)
 *
 * @see Draft для доменной модели
 * @see DraftEntity для JPA сущности
 */
@Component
public class DraftPersistenceMapper {

    public DraftEntity toEntity(Draft domain) {
        return DraftEntity.builder()
                .id(domain.getId())
                .barcode(domain.getBarcode())
                .draftCreatorId(domain.getDraftCreatorId())
                .senderUserId(domain.getSenderUserId())
                .recipientUserId(domain.getRecipientUserId())
                .recipientAddress(domain.getRecipientAddress())
                .weightDeclared(domain.getWeightDeclared())
                .dimensions(domain.getDimensions())
                .pricingRuleId(domain.getPricingRuleId())
                .estimatedPrice(domain.getEstimatedPrice())
                .draftStatus(domain.getDraftStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Draft toDomain(DraftEntity entity) {
        return Draft.builder()
                .id(entity.getId())
                .barcode(entity.getBarcode())
                .draftCreatorId(entity.getDraftCreatorId())
                .senderUserId(entity.getSenderUserId())
                .recipientUserId(entity.getRecipientUserId())
                .recipientAddress(entity.getRecipientAddress())
                .weightDeclared(entity.getWeightDeclared())
                .dimensions(entity.getDimensions())
                .pricingRuleId(entity.getPricingRuleId())
                .estimatedPrice(entity.getEstimatedPrice())
                .draftStatus(entity.getDraftStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
