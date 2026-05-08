package com.logistics.corebusiness.rating.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * JPA-сущность рейтинга для таблицы rating_service.ratings_reviews.
 *
 * <h2>Маппинг</h2>
 * - schema: rating_service
 * - table: ratings_reviews
 * - sequence: rating_service.ratings_reviews_review_id_seq
 *
 * <h2>Ограничения БД</h2>
 * - rating CHECK (rating BETWEEN 1 AND 5)
 * - waybill_id FK -> waybill_service.waybills
 * - user_id FK -> user_management.users
 */
@Entity
@Table(
        name = "ratings_reviews",
        schema = "rating_service"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rating_seq")
    @SequenceGenerator(
            name = "rating_seq",
            sequenceName = "ratings_reviews_review_id_seq",
            schema = "rating_service",
            allocationSize = 1
    )
    @Column(name = "review_id")
    private Long id;

    @Column(name = "waybill_id")
    private Long waybillId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "rating")
    private Integer score;

    @Column(name = "review_text")
    private String reviewText;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
