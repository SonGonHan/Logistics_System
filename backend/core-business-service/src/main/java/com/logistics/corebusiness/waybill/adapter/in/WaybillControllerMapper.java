package com.logistics.corebusiness.waybill.adapter.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.domain.Waybill;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между Domain Waybill и DTO.
 *
 * <h2>Назначение</h2>
 * Конвертирует Waybill -> WaybillResponse.
 *
 * @see Waybill для доменной модели
 * @see WaybillResponse для DTO ответа
 */
@Component
public class WaybillControllerMapper {

    public static WaybillResponse toResponse(Waybill domain) {
        return WaybillResponse.builder()
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
}
