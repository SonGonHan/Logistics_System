package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DraftPersistenceMapper: юнит-тесты")
class DraftPersistenceMapperTest {

    private DraftPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DraftPersistenceMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        Draft domain = Draft.builder()
                .id(1L)
                .barcode("BC-2024-000001")
                .draftCreatorId(100L)
                .senderUserId(200L)
                .recipientUserId(300L)
                .recipientAddress("Москва, ул. Ленина, д. 1")
                .pricingRuleId(10L)
                .estimatedPrice(BigDecimal.valueOf(350.00))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DraftEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getBarcode()).isEqualTo("BC-2024-000001");
        assertThat(entity.getDraftCreatorId()).isEqualTo(100L);
        assertThat(entity.getSenderUserId()).isEqualTo(200L);
        assertThat(entity.getRecipientUserId()).isEqualTo(300L);
        assertThat(entity.getRecipientAddress()).isEqualTo("Москва, ул. Ленина, д. 1");
        assertThat(entity.getPricingRuleId()).isEqualTo(10L);
        assertThat(entity.getEstimatedPrice()).isEqualByComparingTo(BigDecimal.valueOf(350.00));
        assertThat(entity.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        DraftEntity entity = DraftEntity.builder()
                .id(2L)
                .barcode("BC-2024-000002")
                .draftCreatorId(101L)
                .senderUserId(201L)
                .recipientUserId(301L)
                .recipientAddress("Санкт-Петербург, пр. Невский, д. 20")
                .pricingRuleId(11L)
                .estimatedPrice(BigDecimal.valueOf(650.00))
                .draftStatus(DraftStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        Draft domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getBarcode()).isEqualTo("BC-2024-000002");
        assertThat(domain.getDraftCreatorId()).isEqualTo(101L);
        assertThat(domain.getSenderUserId()).isEqualTo(201L);
        assertThat(domain.getRecipientUserId()).isEqualTo(301L);
        assertThat(domain.getRecipientAddress()).isEqualTo("Санкт-Петербург, пр. Невский, д. 20");
        assertThat(domain.getPricingRuleId()).isEqualTo(11L);
        assertThat(domain.getEstimatedPrice()).isEqualByComparingTo(BigDecimal.valueOf(650.00));
        assertThat(domain.getDraftStatus()).isEqualTo(DraftStatus.CONFIRMED);
        assertThat(domain.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать черновик с null значениями")
    void shouldMapDraftWithNullValues() {
        // Given
        Draft domain = Draft.builder()
                .id(3L)
                .barcode("BC-2024-000003")
                .draftCreatorId(102L)
                .senderUserId(202L)
                .recipientUserId(302L)
                .recipientAddress("Казань, ул. Пушкина, д. 5")
                .pricingRuleId(null)
                .estimatedPrice(null)
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DraftEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getPricingRuleId()).isNull();
        assertThat(entity.getEstimatedPrice()).isNull();
        assertThat(entity.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
    }

    @Test
    @DisplayName("Должен корректно обрабатывать все статусы черновика")
    void shouldMapAllDraftStatuses() {
        for (DraftStatus status : DraftStatus.values()) {
            Draft domain = Draft.builder()
                    .id(1L)
                    .barcode("BC-2024-TEST")
                    .draftCreatorId(100L)
                    .senderUserId(200L)
                    .recipientUserId(300L)
                    .recipientAddress("Test Address")
                    .draftStatus(status)
                    .createdAt(LocalDateTime.now())
                    .build();

            DraftEntity entity = mapper.toEntity(domain);
            Draft mappedBack = mapper.toDomain(entity);

            assertThat(mappedBack.getDraftStatus()).isEqualTo(status);
        }
    }
}