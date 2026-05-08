package com.logistics.corebusiness.rating.adapter.in;

import com.logistics.corebusiness.rating.domain.Rating;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RatingControllerMapper - модульные тесты")
class RatingControllerMapperTest {

    @Test
    @DisplayName("Должен корректно преобразовать Rating в RatingResponse")
    void shouldMapRatingToResponse() {
        var createdAt = LocalDateTime.now();
        var rating = Rating.builder()
                .id(1L)
                .waybillId(10L)
                .ratingCreatorId(20L)
                .score(5)
                .comment("Отлично")
                .createdAt(createdAt)
                .build();

        var response = RatingControllerMapper.toResponse(rating);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.waybillId()).isEqualTo(10L);
        assertThat(response.userId()).isEqualTo(20L);
        assertThat(response.score()).isEqualTo(5);
        assertThat(response.comment()).isEqualTo("Отлично");
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }
}
