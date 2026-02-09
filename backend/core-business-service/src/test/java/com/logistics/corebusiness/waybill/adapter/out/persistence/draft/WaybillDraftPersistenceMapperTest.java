package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.WaybillDraft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WaybillDraftPersistenceMapper: юнит-тесты")
class WaybillDraftPersistenceMapperTest {

    private WaybillDraftPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WaybillDraftPersistenceMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        Dimensions dimensions = Dimensions.of(
                BigDecimal.valueOf(25.00),
                BigDecimal.valueOf(35.00),
                BigDecimal.valueOf(45.00)
        );

        WaybillDraft domain = WaybillDraft.builder()
                .id(1L)
                .barcode("BC-2024-000001")
                .draftCreatorId(100L)
                .senderUserId(200L)
                .recipientUserId(300L)
                .recipientAddress("Москва, ул. Ленина, д. 1")
                .weightDeclared(BigDecimal.valueOf(4.50))
                .dimensions(dimensions)
                .pricingRuleId(10L)
                .estimatedPrice(BigDecimal.valueOf(450.00))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        WaybillDraftEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getBarcode()).isEqualTo("BC-2024-000001");
        assertThat(entity.getDraftCreatorId()).isEqualTo(100L);
        assertThat(entity.getSenderUserId()).isEqualTo(200L);
        assertThat(entity.getRecipientUserId()).isEqualTo(300L);
        assertThat(entity.getRecipientAddress()).isEqualTo("Москва, ул. Ленина, д. 1");
        assertThat(entity.getWeightDeclared()).isEqualByComparingTo(BigDecimal.valueOf(4.50));
        assertThat(entity.getDimensions()).isEqualTo(dimensions);
        assertThat(entity.getPricingRuleId()).isEqualTo(10L);
        assertThat(entity.getEstimatedPrice()).isEqualByComparingTo(BigDecimal.valueOf(450.00));
        assertThat(entity.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        Dimensions dimensions = Dimensions.of(
                BigDecimal.valueOf(15.00),
                BigDecimal.valueOf(25.00),
                BigDecimal.valueOf(35.00)
        );

        WaybillDraftEntity entity = WaybillDraftEntity.builder()
                .id(2L)
                .barcode("BC-2024-000002")
                .draftCreatorId(101L)
                .senderUserId(201L)
                .recipientUserId(301L)
                .recipientAddress("Санкт-Петербург, пр. Невский, д. 20")
                .weightDeclared(BigDecimal.valueOf(2.75))
                .dimensions(dimensions)
                .pricingRuleId(11L)
                .estimatedPrice(BigDecimal.valueOf(300.00))
                .draftStatus(DraftStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        WaybillDraft domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getBarcode()).isEqualTo("BC-2024-000002");
        assertThat(domain.getDraftCreatorId()).isEqualTo(101L);
        assertThat(domain.getSenderUserId()).isEqualTo(201L);
        assertThat(domain.getRecipientUserId()).isEqualTo(301L);
        assertThat(domain.getRecipientAddress()).isEqualTo("Санкт-Петербург, пр. Невский, д. 20");
        assertThat(domain.getWeightDeclared()).isEqualByComparingTo(BigDecimal.valueOf(2.75));
        assertThat(domain.getDimensions()).isEqualTo(dimensions);
        assertThat(domain.getPricingRuleId()).isEqualTo(11L);
        assertThat(domain.getEstimatedPrice()).isEqualByComparingTo(BigDecimal.valueOf(300.00));
        assertThat(domain.getDraftStatus()).isEqualTo(DraftStatus.CONFIRMED);
        assertThat(domain.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать черновик с null значениями")
    void shouldMapDraftWithNullValues() {
        // Given
        WaybillDraft domain = WaybillDraft.builder()
                .id(3L)
                .barcode("BC-2024-000003")
                .draftCreatorId(102L)
                .senderUserId(202L)
                .recipientUserId(302L)
                .recipientAddress("Казань, ул. Пушкина, д. 5")
                .weightDeclared(null)
                .dimensions(null)
                .pricingRuleId(null)
                .estimatedPrice(null)
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        WaybillDraftEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getWeightDeclared()).isNull();
        assertThat(entity.getDimensions()).isNull();
        assertThat(entity.getPricingRuleId()).isNull();
        assertThat(entity.getEstimatedPrice()).isNull();
        assertThat(entity.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
    }

    @Test
    @DisplayName("Должен корректно обрабатывать все статусы черновика")
    void shouldMapAllDraftStatuses() {
        for (DraftStatus status : DraftStatus.values()) {
            // Given
            WaybillDraft domain = WaybillDraft.builder()
                    .id(1L)
                    .barcode("BC-2024-TEST")
                    .draftCreatorId(100L)
                    .senderUserId(200L)
                    .recipientUserId(300L)
                    .recipientAddress("Test Address")
                    .draftStatus(status)
                    .createdAt(LocalDateTime.now())
                    .build();

            // When
            WaybillDraftEntity entity = mapper.toEntity(domain);
            WaybillDraft mappedBack = mapper.toDomain(entity);

            // Then
            assertThat(mappedBack.getDraftStatus()).isEqualTo(status);
        }
    }
}
