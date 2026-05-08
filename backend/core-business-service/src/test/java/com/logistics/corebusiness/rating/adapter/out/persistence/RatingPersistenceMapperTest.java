package com.logistics.corebusiness.rating.adapter.out.persistence;

import com.logistics.corebusiness.rating.domain.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RatingPersistenceMapper - модульные тесты")
class RatingPersistenceMapperTest {

    private RatingPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RatingPersistenceMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        var createdAt = LocalDateTime.now();
        var domain = Rating.builder()
                .id(1L)
                .waybillId(10L)
                .ratingCreatorId(20L)
                .score(5)
                .comment("Отлично")
                .createdAt(createdAt)
                .build();

        var entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getWaybillId()).isEqualTo(10L);
        assertThat(entity.getUserId()).isEqualTo(20L);
        assertThat(entity.getScore()).isEqualTo(5);
        assertThat(entity.getReviewText()).isEqualTo("Отлично");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        var createdAt = LocalDateTime.now();
        var entity = RatingEntity.builder()
                .id(1L)
                .waybillId(10L)
                .userId(20L)
                .score(5)
                .reviewText("Отлично")
                .createdAt(createdAt)
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getWaybillId()).isEqualTo(10L);
        assertThat(domain.getRatingCreatorId()).isEqualTo(20L);
        assertThat(domain.getScore()).isEqualTo(5);
        assertThat(domain.getComment()).isEqualTo("Отлично");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
    }
}
