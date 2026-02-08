package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.domain.WaybillDraft;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между WaybillDraft (domain) и WaybillDraftEntity (JPA).
 */
@Component
public class WaybillDraftPersistenceMapper {

    public WaybillDraftEntity toEntity(WaybillDraft domain) {
        return WaybillDraftEntity.builder()
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

    public WaybillDraft toDomain(WaybillDraftEntity entity) {
        return WaybillDraft.builder()
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
