package com.logistics.corebusiness.waybill.adapter.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DetailedDraftResponse;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.domain.Draft;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между Domain Draft и DTO.
 *
 * <h2>Назначение</h2>
 * Конвертирует Draft → DraftResponse / DetailedDraftResponse.
 *
 * @see Draft для доменной модели
 * @see DraftResponse для базового DTO
 * @see DetailedDraftResponse для расширенного DTO
 */
@Component
public class DraftControllerMapper {

    public static DraftResponse toResponse(Draft domain) {
        return DraftResponse.builder()
                .id(domain.getId())
                .barcode(domain.getBarcode())
                .recipientUserId(domain.getRecipientUserId())
                .recipientAddress(domain.getRecipientAddress())
                .estimatedPrice(domain.getEstimatedPrice())
                .draftStatus(domain.getDraftStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public static DetailedDraftResponse toDetailedResponse(Draft domain) {
        return DetailedDraftResponse.builder()
                .id(domain.getId())
                .barcode(domain.getBarcode())
                .draftCreatorId(domain.getDraftCreatorId())
                .senderUserId(domain.getSenderUserId())
                .recipientUserId(domain.getRecipientUserId())
                .recipientAddress(domain.getRecipientAddress())
                .pricingRuleId(domain.getPricingRuleId())
                .estimatedPrice(domain.getEstimatedPrice())
                .draftStatus(domain.getDraftStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}