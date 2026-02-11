package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.GetUserDraftListCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserDraftListService - тестирование получения списка черновиков пользователя")
class GetUserDraftListServiceTest {

    @Mock
    private DraftRepository repository;

    private GetUserDraftListService service;

    @BeforeEach
    void setUp() {
        service = new GetUserDraftListService(repository);
    }

    @Test
    @DisplayName("Должен вернуть все черновики пользователя")
    void shouldReturnAllUserDrafts() {
        // Given
        Long userId = 100L;

        var draft1 = createDraft(1L, userId, "DRF-260209-111111", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 10, 0));
        var draft2 = createDraft(2L, userId, "DRF-260209-222222", DraftStatus.CONFIRMED,
                LocalDateTime.of(2026, 2, 9, 11, 0));
        var draft3 = createDraft(3L, userId, "DRF-260209-333333", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 12, 0));

        when(repository.findByDraftCreatorId(userId)).thenReturn(Arrays.asList(draft1, draft2, draft3));

        var command = GetUserDraftListCommand.builder()
                .userId(userId)
                .status(null)
                .build();

        // When
        List<DraftResponse> result = service.get(command);

        // Then
        assertThat(result).hasSize(3);
        // Проверяем сортировку (newest first)
        assertThat(result.get(0).barcode()).isEqualTo("DRF-260209-333333"); // 12:00 - самый новый
        assertThat(result.get(1).barcode()).isEqualTo("DRF-260209-222222"); // 11:00
        assertThat(result.get(2).barcode()).isEqualTo("DRF-260209-111111"); // 10:00 - самый старый

        verify(repository).findByDraftCreatorId(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен вернуть пустой список, если у пользователя нет черновиков")
    void shouldReturnEmptyListWhenUserHasNoDrafts() {
        // Given
        Long userId = 200L;

        when(repository.findByDraftCreatorId(userId)).thenReturn(Collections.emptyList());

        var command = GetUserDraftListCommand.builder()
                .userId(userId)
                .status(null)
                .build();

        // When
        List<DraftResponse> result = service.get(command);

        // Then
        assertThat(result).isEmpty();

        verify(repository).findByDraftCreatorId(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен отфильтровать черновики по статусу PENDING")
    void shouldFilterDraftsByPendingStatus() {
        // Given
        Long userId = 100L;

        var draft1 = createDraft(1L, userId, "DRF-260209-111111", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 10, 0));
        var draft2 = createDraft(2L, userId, "DRF-260209-222222", DraftStatus.CONFIRMED,
                LocalDateTime.of(2026, 2, 9, 11, 0));
        var draft3 = createDraft(3L, userId, "DRF-260209-333333", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 12, 0));
        var draft4 = createDraft(4L, userId, "DRF-260209-444444", DraftStatus.CANCELLED,
                LocalDateTime.of(2026, 2, 9, 13, 0));

        when(repository.findByDraftCreatorId(userId)).thenReturn(Arrays.asList(draft1, draft2, draft3, draft4));

        var command = GetUserDraftListCommand.builder()
                .userId(userId)
                .status(DraftStatus.PENDING)
                .build();

        // When
        List<DraftResponse> result = service.get(command);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).barcode()).isEqualTo("DRF-260209-333333"); // Только PENDING
        assertThat(result.get(1).barcode()).isEqualTo("DRF-260209-111111"); // Только PENDING
        assertThat(result).allMatch(r -> r.draftStatus() == DraftStatus.PENDING);

        verify(repository).findByDraftCreatorId(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен отфильтровать черновики по статусу CONFIRMED")
    void shouldFilterDraftsByConfirmedStatus() {
        // Given
        Long userId = 100L;

        var draft1 = createDraft(1L, userId, "DRF-260209-111111", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 10, 0));
        var draft2 = createDraft(2L, userId, "DRF-260209-222222", DraftStatus.CONFIRMED,
                LocalDateTime.of(2026, 2, 9, 11, 0));
        var draft3 = createDraft(3L, userId, "DRF-260209-333333", DraftStatus.CONFIRMED,
                LocalDateTime.of(2026, 2, 9, 12, 0));

        when(repository.findByDraftCreatorId(userId)).thenReturn(Arrays.asList(draft1, draft2, draft3));

        var command = GetUserDraftListCommand.builder()
                .userId(userId)
                .status(DraftStatus.CONFIRMED)
                .build();

        // When
        List<DraftResponse> result = service.get(command);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.draftStatus() == DraftStatus.CONFIRMED);

        verify(repository).findByDraftCreatorId(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен отфильтровать черновики по статусу CANCELLED")
    void shouldFilterDraftsByCancelledStatus() {
        // Given
        Long userId = 100L;

        var draft1 = createDraft(1L, userId, "DRF-260209-111111", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 10, 0));
        var draft2 = createDraft(2L, userId, "DRF-260209-222222", DraftStatus.CANCELLED,
                LocalDateTime.of(2026, 2, 9, 11, 0));

        when(repository.findByDraftCreatorId(userId)).thenReturn(Arrays.asList(draft1, draft2));

        var command = GetUserDraftListCommand.builder()
                .userId(userId)
                .status(DraftStatus.CANCELLED)
                .build();

        // When
        List<DraftResponse> result = service.get(command);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).barcode()).isEqualTo("DRF-260209-222222");
        assertThat(result.get(0).draftStatus()).isEqualTo(DraftStatus.CANCELLED);

        verify(repository).findByDraftCreatorId(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен вернуть пустой список, если нет черновиков с указанным статусом")
    void shouldReturnEmptyListWhenNoMatchingStatus() {
        // Given
        Long userId = 100L;

        var draft1 = createDraft(1L, userId, "DRF-260209-111111", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 10, 0));
        var draft2 = createDraft(2L, userId, "DRF-260209-222222", DraftStatus.PENDING,
                LocalDateTime.of(2026, 2, 9, 11, 0));

        when(repository.findByDraftCreatorId(userId)).thenReturn(Arrays.asList(draft1, draft2));

        var command = GetUserDraftListCommand.builder()
                .userId(userId)
                .status(DraftStatus.CONFIRMED)
                .build();

        // When
        List<DraftResponse> result = service.get(command);

        // Then
        assertThat(result).isEmpty();

        verify(repository).findByDraftCreatorId(userId);
        verifyNoMoreInteractions(repository);
    }

    private Draft createDraft(Long id, Long creatorId, String barcode, DraftStatus status, LocalDateTime createdAt) {
        return Draft.builder()
                .id(id)
                .barcode(barcode)
                .draftCreatorId(creatorId)
                .senderUserId(creatorId)
                .recipientUserId(999L)
                .recipientAddress("Тестовый адрес")
                .weightDeclared(new BigDecimal("1.0"))
                .dimensions(null)
                .estimatedPrice(new BigDecimal("500.00"))
                .draftStatus(status)
                .createdAt(createdAt)
                .build();
    }
}
