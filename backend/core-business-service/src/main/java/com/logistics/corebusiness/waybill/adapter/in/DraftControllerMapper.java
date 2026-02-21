package com.logistics.corebusiness.waybill.adapter.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DetailedDraftResponse;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DimensionsDto;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.corebusiness.waybill.domain.Draft;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между Domain Draft и DTO.
 *
 * <h2>Назначение</h2>
 * Конвертирует Draft → DraftResponse / DetailedDraftResponse.
 * Поддерживает преобразование Dimensions → DimensionsDto.
 *
 * <h2>Методы</h2>
 * - toResponse(draft) - Базовая информация для клиентов
 * - toDetailedResponse(draft) - Полная информация для работников
 * - toDimensionsDto(dimensions) - Преобразование Value Object
 * - toDimensions(dto) - Обратное преобразование
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
                .weightDeclared(domain.getWeightDeclared())
                .dimensions(toDimensionsDto(domain.getDimensions()))
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
                .weightDeclared(domain.getWeightDeclared())
                .dimensions(toDimensionsDto(domain.getDimensions()))
                .pricingRuleId(domain.getPricingRuleId())
                .estimatedPrice(domain.getEstimatedPrice())
                .draftStatus(domain.getDraftStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }


    public static DimensionsDto toDimensionsDto(Dimensions dimensions) {
        if (dimensions == null) {
            return null;
        }
        return DimensionsDto.builder()
                .length(dimensions.length())
                .width(dimensions.width())
                .height(dimensions.height())
                .build();
    }

    public static Dimensions toDimensions(DimensionsDto dto) {
        if (dto == null) {
            return null;
        }
        return new Dimensions(dto.length(), dto.width(), dto.height());
    }
}
