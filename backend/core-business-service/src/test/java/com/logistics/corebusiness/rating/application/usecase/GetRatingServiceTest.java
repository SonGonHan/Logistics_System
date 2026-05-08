package com.logistics.corebusiness.rating.application.usecase;

import com.logistics.corebusiness.rating.application.exception.RatingNotFoundException;
import com.logistics.corebusiness.rating.application.port.in.command.GetRatingCommand;
import com.logistics.corebusiness.rating.application.port.out.RatingRepository;
import com.logistics.corebusiness.rating.domain.Rating;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetRatingService - модульные тесты")
class GetRatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private GetRatingService getRatingService;

    @Test
    @DisplayName("Должен успешно получить рейтинг по ID накладной")
    void shouldGetRatingByWaybillIdSuccessfully() {
        var createdAt = LocalDateTime.now();
        when(ratingRepository.findByWaybillId(10L)).thenReturn(Optional.of(Rating.builder()
                .id(1L)
                .waybillId(10L)
                .ratingCreatorId(2L)
                .score(4)
                .comment("Хорошо")
                .createdAt(createdAt)
                .build()));

        var response = getRatingService.get(GetRatingCommand.builder().waybillId(10L).build());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.waybillId()).isEqualTo(10L);
        assertThat(response.userId()).isEqualTo(2L);
        assertThat(response.score()).isEqualTo(4);
        assertThat(response.comment()).isEqualTo("Хорошо");
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Должен выбросить исключение когда рейтинг не найден")
    void shouldThrowWhenRatingNotFound() {
        when(ratingRepository.findByWaybillId(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getRatingService.get(GetRatingCommand.builder().waybillId(10L).build()))
                .isInstanceOf(RatingNotFoundException.class)
                .hasMessage("Rating not found for waybill id: 10");
    }
}
