package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.domain.Waybill;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между Waybill (domain) и WaybillEntity (JPA).
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
                .weightActual(domain.getWeightActual())
                .dimensions(domain.getDimensions())
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
                .weightActual(entity.getWeightActual())
                .dimensions(entity.getDimensions())
                .pricingRuleId(entity.getPricingRuleId())
                .finalPrice(entity.getFinalPrice())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .acceptedAt(entity.getAcceptedAt())
                .build();
    }
}
