package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.command.DeleteDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteDraftService - тестирование удаления черновика накладной")
class DeleteDraftServiceTest {

    @Mock
    private DraftRepository repository;

    @Mock
    private CreateAuditLogUseCase auditLogUseCase;

    @Captor
    private ArgumentCaptor<CreateAuditLogCommand> auditCaptor;

    private DeleteDraftService service;

    @BeforeEach
    void setUp() {
        service = new DeleteDraftService(repository, auditLogUseCase);
    }

    @Test
    @DisplayName("Должен успешно удалить черновик в статусе PENDING")
    void shouldDeleteDraftSuccessfully() {
        // Given
        Long draftId = 1L;
        Long userId = 100L;

        var draft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-123456")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .pricingRuleId(1L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(draft));

        var command = DeleteDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .build();

        // When
        service.delete(command);

        // Then
        verify(repository).findById(draftId);
        verify(auditLogUseCase).create(any(CreateAuditLogCommand.class));
        verify(repository).delete(draft);
        verifyNoMoreInteractions(repository, auditLogUseCase);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если черновик не найден")
    void shouldThrowExceptionWhenDraftNotFound() {
        // Given
        Long draftId = 999L;
        Long userId = 100L;

        when(repository.findById(draftId)).thenReturn(Optional.empty());

        var command = DeleteDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .build();

        // When / Then
        assertThatThrownBy(() -> service.delete(command))
                .isInstanceOf(DraftNotFoundException.class)
                .hasMessageContaining("Draft not found with id: 999");

        verify(repository).findById(draftId);
        verify(repository, never()).delete(any());
        verify(auditLogUseCase, never()).create(any());
    }

    @Test
    @DisplayName("Должен записать аудит удаления черновика")
    void shouldAuditDraftDeletion() {
        // Given
        Long draftId = 1L;
        Long userId = 100L;
        String barcode = "DRF-260209-123456";

        var draft = Draft.builder()
                .id(draftId)
                .barcode(barcode)
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .pricingRuleId(1L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(draft));

        var command = DeleteDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .build();

        // When
        service.delete(command);

        // Then
        verify(auditLogUseCase).create(auditCaptor.capture());

        var auditCommand = auditCaptor.getValue();
        assertThat(auditCommand.actionTypeName()).isEqualTo("DRAFT_CANCEL");
        assertThat(auditCommand.userId()).isEqualTo(userId);
        assertThat(auditCommand.tableName()).isEqualTo("waybill_drafts");
        assertThat(auditCommand.recordId()).isEqualTo(draftId);
        assertThat(auditCommand.newValues())
                .containsEntry("draftId", draftId)
                .containsEntry("barcode", barcode);
    }

    @Test
    @DisplayName("Должен записать аудит перед удалением черновика")
    void shouldAuditBeforeDelete() {
        // Given
        Long draftId = 1L;
        Long userId = 100L;

        var draft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-123456")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("Адрес")
                .pricingRuleId(1L)
                .estimatedPrice(new BigDecimal("350.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(draft));

        var command = DeleteDraftCommand.builder()
                .draftId(draftId)
                .userId(userId)
                .build();

        // When
        service.delete(command);

        // Then
        var inOrder = inOrder(auditLogUseCase, repository);
        inOrder.verify(auditLogUseCase).create(any());
        inOrder.verify(repository).delete(any());
    }

}
