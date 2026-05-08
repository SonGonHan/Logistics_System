package com.logistics.corebusiness.rating.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.rating.application.exception.RatingDuplicateException;
import com.logistics.corebusiness.rating.application.exception.RatingValidationException;
import com.logistics.corebusiness.rating.application.port.in.command.SubmitRatingCommand;
import com.logistics.corebusiness.rating.application.port.out.RatingRepository;
import com.logistics.corebusiness.rating.domain.Rating;
import com.logistics.corebusiness.waybill.application.exception.WaybillNotFoundException;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmitRatingService - модульные тесты")
class SubmitRatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private WaybillRepository waybillRepository;

    @Mock
    private CreateAuditLogUseCase auditLogUseCase;

    @InjectMocks
    private SubmitRatingService submitRatingService;

    @Captor
    private ArgumentCaptor<Rating> ratingCaptor;

    @Captor
    private ArgumentCaptor<CreateAuditLogCommand> auditCaptor;

    private static final Long USER_ID = 1L;
    private static final Long WAYBILL_ID = 10L;
    private static final Integer SCORE = 5;
    private static final String COMMENT = "Отличная доставка!";

    @Test
    @DisplayName("Должен успешно создать рейтинг")
    void shouldSubmitRatingSuccessfully() {
        when(waybillRepository.findById(WAYBILL_ID)).thenReturn(Optional.of(createWaybill(WaybillStatus.DELIVERED)));
        when(ratingRepository.existsByWaybillId(WAYBILL_ID)).thenReturn(false);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            var rating = invocation.getArgument(0, Rating.class);
            rating.setId(99L);
            return rating;
        });

        var response = submitRatingService.submit(createCommand(COMMENT));

        verify(ratingRepository).save(ratingCaptor.capture());
        var savedRating = ratingCaptor.getValue();
        assertThat(savedRating.getWaybillId()).isEqualTo(WAYBILL_ID);
        assertThat(savedRating.getRatingCreatorId()).isEqualTo(USER_ID);
        assertThat(savedRating.getScore()).isEqualTo(SCORE);
        assertThat(savedRating.getComment()).isEqualTo(COMMENT);

        verify(auditLogUseCase).create(auditCaptor.capture());
        var audit = auditCaptor.getValue();
        assertThat(audit.actionTypeName()).isEqualTo("RATING_SUBMIT");
        assertThat(audit.userId()).isEqualTo(USER_ID);
        assertThat(audit.tableName()).isEqualTo("ratings_reviews");
        assertThat(audit.recordId()).isEqualTo(99L);

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.waybillId()).isEqualTo(WAYBILL_ID);
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.score()).isEqualTo(SCORE);
        assertThat(response.comment()).isEqualTo(COMMENT);
    }

    @Test
    @DisplayName("Должен выбросить исключение когда накладная не найдена")
    void shouldThrowWhenWaybillNotFound() {
        when(waybillRepository.findById(WAYBILL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submitRatingService.submit(createCommand(COMMENT)))
                .isInstanceOf(WaybillNotFoundException.class)
                .hasMessage("Waybill not found with id: 10");

        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение когда накладная не доставлена")
    void shouldThrowWhenWaybillNotDelivered() {
        for (var status : new WaybillStatus[]{
                WaybillStatus.IN_TRANSIT,
                WaybillStatus.CANCELLED,
                WaybillStatus.ACCEPTED_AT_PVZ
        }) {
            when(waybillRepository.findById(WAYBILL_ID)).thenReturn(Optional.of(createWaybill(status)));

            assertThatThrownBy(() -> submitRatingService.submit(createCommand(COMMENT)))
                    .isInstanceOf(RatingValidationException.class)
                    .hasMessage("Cannot rate waybill 10: status is " + status.name() + ", expected DELIVERED");
        }

        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить исключение когда рейтинг уже существует")
    void shouldThrowWhenRatingAlreadyExists() {
        when(waybillRepository.findById(WAYBILL_ID)).thenReturn(Optional.of(createWaybill(WaybillStatus.DELIVERED)));
        when(ratingRepository.existsByWaybillId(WAYBILL_ID)).thenReturn(true);

        assertThatThrownBy(() -> submitRatingService.submit(createCommand(COMMENT)))
                .isInstanceOf(RatingDuplicateException.class)
                .hasMessage("Rating already exists for waybill id: 10");

        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен успешно передать null в качестве отзыва")
    void shouldPassCommentAsNull() {
        when(waybillRepository.findById(WAYBILL_ID)).thenReturn(Optional.of(createWaybill(WaybillStatus.DELIVERED)));
        when(ratingRepository.existsByWaybillId(WAYBILL_ID)).thenReturn(false);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            var rating = invocation.getArgument(0, Rating.class);
            rating.setId(100L);
            return rating;
        });

        var response = submitRatingService.submit(createCommand(null));

        verify(ratingRepository).save(ratingCaptor.capture());
        assertThat(ratingCaptor.getValue().getComment()).isNull();
        assertThat(response.comment()).isNull();
    }

    private SubmitRatingCommand createCommand(String comment) {
        return SubmitRatingCommand.builder()
                .waybillId(WAYBILL_ID)
                .userId(USER_ID)
                .score(SCORE)
                .comment(comment)
                .build();
    }

    private Waybill createWaybill(WaybillStatus status) {
        return Waybill.builder()
                .id(WAYBILL_ID)
                .waybillNumber("WB-260501-000010")
                .waybillCreatorId(USER_ID)
                .senderUserId(USER_ID)
                .recipientUserId(2L)
                .recipientAddress("Test Address")
                .status(status)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();
    }
}
