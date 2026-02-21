package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.application.port.in.command.CreateDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.application.util.BarcodeGenerator;
import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.shared.pricing_rule.PricingRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateDraftService - тестирование создания черновика накладной")
class CreateDraftServiceTest {

    @Mock
    private DraftRepository repository;

    @Mock
    private BarcodeGenerator barcodeGenerator;

    @Mock
    private PricingRuleService pricingRuleService;

    @Captor
    private ArgumentCaptor<Draft> draftCaptor;

    private CreateDraftService createDraftService;

    @BeforeEach
    void setUp() {
        createDraftService = new CreateDraftService(
                repository,
                barcodeGenerator,
                pricingRuleService);
    }

    @Test
    @DisplayName("Должен успешно создать черновик с корректными полями")
    void shouldCreateDraftSuccessfully() {
        // Given
        String generatedBarcode = "DRF-260209-123456";
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn(generatedBarcode);
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("2.5")))
                .thenReturn(new BigDecimal("1250.00"));

        var dimensions = new Dimensions(
                new BigDecimal("30"),
                new BigDecimal("40"),
                new BigDecimal("50")
        );

        var command = CreateDraftCommand.builder()
                .draftCreatorId(1L)
                .senderUserId(2L)
                .recipientUserId(3L)
                .recipientAddress("г. Москва, ул. Тестовая, д. 1")
                .weightDeclared(new BigDecimal("2.5"))
                .pricingRuleId(pricingRuleId)
                .dimensions(dimensions)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(barcodeGenerator).generate();
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("2.5"));
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        assertThat(savedDraft.getBarcode()).isEqualTo(generatedBarcode);
        assertThat(savedDraft.getDraftCreatorId()).isEqualTo(1L);
        assertThat(savedDraft.getSenderUserId()).isEqualTo(2L);
        assertThat(savedDraft.getRecipientUserId()).isEqualTo(3L);
        assertThat(savedDraft.getRecipientAddress()).isEqualTo("г. Москва, ул. Тестовая, д. 1");
        assertThat(savedDraft.getWeightDeclared()).isEqualByComparingTo("2.5");
        assertThat(savedDraft.getDimensions()).isEqualTo(dimensions);
        assertThat(savedDraft.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
        assertThat(savedDraft.getCreatedAt()).isNotNull();
        assertThat(savedDraft.getPricingRuleId()).isEqualTo(pricingRuleId);
        assertThat(savedDraft.getEstimatedPrice()).isEqualByComparingTo("1250.00");

        verifyNoMoreInteractions(repository, barcodeGenerator, pricingRuleService);
    }

    @Test
    @DisplayName("Должен рассчитать цену на основе веса (500.00 за кг)")
    void shouldCalculateEstimatedPrice() {
        // Given
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn("DRF-260209-999999");
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("3.0")))
                .thenReturn(new BigDecimal("1500.00"));

        var command = CreateDraftCommand.builder()
                .draftCreatorId(1L)
                .senderUserId(1L)
                .recipientUserId(2L)
                .recipientAddress("г. Санкт-Петербург, ул. Невская, д. 5")
                .weightDeclared(new BigDecimal("3.0"))
                .pricingRuleId(pricingRuleId)
                .dimensions(null)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("3.0"));
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        // Ожидаемая цена: 3.0 кг * 500.00 = 1500.00
        assertThat(savedDraft.getEstimatedPrice()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("Должен создать черновик без габаритов (dimensions = null)")
    void shouldCreateDraftWithoutDimensions() {
        // Given
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn("DRF-260209-111111");
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("1.0")))
                .thenReturn(new BigDecimal("500.00"));

        var command = CreateDraftCommand.builder()
                .draftCreatorId(5L)
                .senderUserId(5L)
                .recipientUserId(6L)
                .recipientAddress("г. Казань, ул. Баумана, д. 10")
                .weightDeclared(new BigDecimal("1.0"))
                .pricingRuleId(pricingRuleId)
                .dimensions(null)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("1.0"));
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        assertThat(savedDraft.getDimensions()).isNull();
        assertThat(savedDraft.getEstimatedPrice()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("Должен установить начальный статус PENDING")
    void shouldSetInitialStatusToPending() {
        // Given
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn("DRF-260209-222222");
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("0.5")))
                .thenReturn(new BigDecimal("250.00"));

        var command = CreateDraftCommand.builder()
                .draftCreatorId(10L)
                .senderUserId(10L)
                .recipientUserId(11L)
                .recipientAddress("г. Екатеринбург, пр. Ленина, д. 50")
                .weightDeclared(new BigDecimal("0.5"))
                .pricingRuleId(pricingRuleId)
                .dimensions(null)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("0.5"));
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        assertThat(savedDraft.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
    }

    @Test
    @DisplayName("Должен создать черновик с минимальным весом")
    void shouldCreateDraftWithMinimalWeight() {
        // Given
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn("DRF-260209-333333");
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("0.1")))
                .thenReturn(new BigDecimal("50.00"));

        var command = CreateDraftCommand.builder()
                .draftCreatorId(20L)
                .senderUserId(20L)
                .recipientUserId(21L)
                .recipientAddress("г. Новосибирск, ул. Красный проспект, д. 1")
                .weightDeclared(new BigDecimal("0.1"))
                .pricingRuleId(pricingRuleId)
                .dimensions(null)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("0.1"));
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        // Цена: 0.1 кг * 500.00 = 50.00
        assertThat(savedDraft.getEstimatedPrice()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Должен создать черновик с большим весом")
    void shouldCreateDraftWithLargeWeight() {
        // Given
        Long pricingRuleId = 1L;
        when(barcodeGenerator.generate()).thenReturn("DRF-260209-444444");
        when(pricingRuleService.calculatePrice(pricingRuleId, new BigDecimal("50.0")))
                .thenReturn(new BigDecimal("25000.00"));

        var command = CreateDraftCommand.builder()
                .draftCreatorId(30L)
                .senderUserId(30L)
                .recipientUserId(31L)
                .recipientAddress("г. Владивосток, ул. Морская, д. 15")
                .weightDeclared(new BigDecimal("50.0"))
                .pricingRuleId(pricingRuleId)
                .dimensions(null)
                .build();

        // When
        createDraftService.create(command);

        // Then
        verify(pricingRuleService).calculatePrice(pricingRuleId, new BigDecimal("50.0"));
        verify(repository).save(draftCaptor.capture());

        Draft savedDraft = draftCaptor.getValue();
        // Цена: 50.0 кг * 500.00 = 25000.00
        assertThat(savedDraft.getEstimatedPrice()).isEqualByComparingTo("25000.00");
    }
}
