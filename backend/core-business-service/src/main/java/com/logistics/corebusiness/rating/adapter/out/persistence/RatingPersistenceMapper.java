package com.logistics.corebusiness.rating.adapter.out.persistence;

import com.logistics.corebusiness.rating.domain.Rating;
import org.springframework.stereotype.Component;

/**
 * Маппер между доменным объектом Rating и JPA-сущностью RatingEntity.
 *
 * <h2>Назначение</h2>
 * Изолирует доменный слой от деталей ORM маппинга.
 *
 * <h2>Маппинг полей</h2>
 * <ul>
 *   <li>Rating.ratingCreatorId -> RatingEntity.userId</li>
 *   <li>Rating.score -> RatingEntity.score</li>
 *   <li>Rating.comment -> RatingEntity.reviewText</li>
 * </ul>
 */
@Component
public class RatingPersistenceMapper {

    public RatingEntity toEntity(Rating domain) {
        return RatingEntity.builder()
                .id(domain.getId())
                .waybillId(domain.getWaybillId())
                .userId(domain.getRatingCreatorId())
                .score(domain.getScore())
                .reviewText(domain.getComment())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Rating toDomain(RatingEntity entity) {
        return Rating.builder()
                .id(entity.getId())
                .waybillId(entity.getWaybillId())
                .ratingCreatorId(entity.getUserId())
                .score(entity.getScore())
                .comment(entity.getReviewText())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
