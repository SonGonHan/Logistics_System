package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.exception.DraftValidationException;
import com.logistics.corebusiness.waybill.application.port.in.command.GetDraftCommand;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetDraftService - тестирование получения черновика накладной")
class GetDraftServiceTest {

    @Mock
    private DraftRepository repository;

    private GetDraftService service;

    @BeforeEach
    void setUp() {
        service = new GetDraftService(repository);
    }

    @Test
    @DisplayName("Должен успешно получить черновик по ID")
    void shouldGetDraftByIdSuccessfully() {
        // Given
        Long draftId = 1L;
        Long userId = 100L;

        var draft = Draft.builder()
                .id(draftId)
                .barcode("DRF-260209-123456")
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(200L)
                .recipientAddress("г. Москва, ул. Тестовая, д. 1")
                .weightDeclared(new BigDecimal("2.5"))
                .dimensions(null)
                .estimatedPrice(new BigDecimal("1250.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(draftId)).thenReturn(Optional.of(draft));

        var command = GetDraftCommand.builder()
                .id(draftId)
                .build();

        // When
        DraftResponse result = service.get(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(draftId);
        assertThat(result.barcode()).isEqualTo("DRF-260209-123456");
        assertThat(result.recipientUserId()).isEqualTo(200L);
        assertThat(result.recipientAddress()).isEqualTo("г. Москва, ул. Тестовая, д. 1");
        assertThat(result.weightDeclared()).isEqualByComparingTo("2.5");
        assertThat(result.estimatedPrice()).isEqualByComparingTo("1250.00");
        assertThat(result.draftStatus()).isEqualTo(DraftStatus.PENDING);

        verify(repository).findById(draftId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен успешно получить черновик по barcode")
    void shouldGetDraftByBarcodeSuccessfully() {
        // Given
        String barcode = "DRF-260209-987654";
        Long userId = 100L;

        var draft = Draft.builder()
                .id(2L)
                .barcode(barcode)
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(300L)
                .recipientAddress("г. Санкт-Петербург, ул. Невская, д. 5")
                .weightDeclared(new BigDecimal("1.0"))
                .dimensions(null)
                .estimatedPrice(new BigDecimal("500.00"))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findByBarcode(barcode)).thenReturn(Optional.of(draft));

        var command = GetDraftCommand.builder()
                .barcode(barcode)
                .build();

        // When
        DraftResponse result = service.get(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.barcode()).isEqualTo(barcode);
        assertThat(result.recipientAddress()).isEqualTo("г. Санкт-Петербург, ул. Невская, д. 5");

        verify(repository).findByBarcode(barcode);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если черновик не найден по ID")
    void shouldThrowExceptionWhenDraftNotFoundById() {
        // Given
        Long draftId = 999L;

        when(repository.findById(draftId)).thenReturn(Optional.empty());

        var command = GetDraftCommand.builder()
                .id(draftId)
                .build();

        // When / Then
        assertThatThrownBy(() -> service.get(command))
                .isInstanceOf(DraftNotFoundException.class)
                .hasMessageContaining("Draft not found with id: 999");

        verify(repository).findById(draftId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если черновик не найден по barcode")
    void shouldThrowExceptionWhenDraftNotFoundByBarcode() {
        // Given
        String barcode = "DRF-260209-NOTFOUND";

        when(repository.findByBarcode(barcode)).thenReturn(Optional.empty());

        var command = GetDraftCommand.builder()
                .barcode(barcode)
                .build();

        // When / Then
        assertThatThrownBy(() -> service.get(command))
                .isInstanceOf(DraftNotFoundException.class)
                .hasMessageContaining("Draft not found with barcode: DRF-260209-NOTFOUND");

        verify(repository).findByBarcode(barcode);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Должен выбросить исключение, если не указаны ни ID, ни barcode")
    void shouldThrowExceptionWhenBothIdAndBarcodeAreNull() {
        // Given
        var command = GetDraftCommand.builder()
                .id(null)
                .barcode(null)
                .build();

        // When / Then
        assertThatThrownBy(() -> service.get(command))
                .isInstanceOf(DraftValidationException.class)
                .hasMessageContaining("Either id or barcode must be provided");

        verifyNoInteractions(repository);
    }
}
