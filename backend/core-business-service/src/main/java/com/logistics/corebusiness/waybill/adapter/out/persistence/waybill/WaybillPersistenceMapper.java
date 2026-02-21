package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.domain.Waybill;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между доменной моделью и JPA сущностью накладной.
 *
 * <h2>Назначение</h2>
 * Обеспечивает изоляцию доменного слоя от деталей JPA:
 * - Доменная модель (Waybill) не знает о JPA аннотациях
 * - JPA сущность (WaybillEntity) не попадает в бизнес-логику
 *
 * @see Waybill для доменной модели
 * @see WaybillEntity для JPA сущности
 */
@Component
public class WaybillPersistenceMapper {

    public WaybillEntity toEntity(Waybill domain) {
        return WaybillEntity.builder()
                .id(domain.getId())
                .waybillNumber(domain.getWaybillNumber())
                .waybillCreatorId(domain.getWaybillCreatorId())
                .senderUserId(domain.getSenderUserId())
                .recipientUserId(domain.getRecipientUserId())
                .recipientAddress(domain.getRecipientAddress())
                .pricingRuleId(domain.getPricingRuleId())
                .finalPrice(domain.getFinalPrice())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .acceptedAt(domain.getAcceptedAt())
                .build();
    }

    public Waybill toDomain(WaybillEntity entity) {
        return Waybill.builder()
                .id(entity.getId())
                .waybillNumber(entity.getWaybillNumber())
                .waybillCreatorId(entity.getWaybillCreatorId())
                .senderUserId(entity.getSenderUserId())
                .recipientUserId(entity.getRecipientUserId())
                .recipientAddress(entity.getRecipientAddress())
                .pricingRuleId(entity.getPricingRuleId())
                .finalPrice(entity.getFinalPrice())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .acceptedAt(entity.getAcceptedAt())
                .build();
    }
}