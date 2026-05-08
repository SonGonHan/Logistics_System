package com.logistics.corebusiness.rating.adapter.in;

import com.logistics.corebusiness.rating.adapter.in.web.dto.RatingResponse;
import com.logistics.corebusiness.rating.domain.Rating;
import org.springframework.stereotype.Component;

/**
 * Маппер контроллера: доменный объект Rating -> RatingResponse DTO.
 *
 * <h2>Назначение</h2>
 * Преобразует доменный объект в DTO для ответа REST API.
 */
@Component
public class RatingControllerMapper {

    public static RatingResponse toResponse(Rating domain) {
        return RatingResponse.builder()
                .id(domain.getId())
                .waybillId(domain.getWaybillId())
                .userId(domain.getRatingCreatorId())
                .score(domain.getScore())
                .comment(domain.getComment())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
