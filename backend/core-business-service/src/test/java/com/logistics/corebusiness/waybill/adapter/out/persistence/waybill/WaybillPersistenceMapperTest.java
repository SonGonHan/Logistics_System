package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WaybillPersistenceMapper: юнит-тесты")
class WaybillPersistenceMapperTest {

    private WaybillPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WaybillPersistenceMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        Dimensions dimensions = Dimensions.of(
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(40.00),
                BigDecimal.valueOf(50.00)
        );

        Waybill domain = Waybill.builder()
                .id(1L)
                .waybillNumber("WB-2024-000001")
                .waybillCreatorId(100L)
                .senderUserId(200L)
                .recipientUserId(300L)
                .recipientAddress("Москва, ул. Ленина, д. 1")
                .weightActual(BigDecimal.valueOf(5.50))
                .dimensions(dimensions)
                .pricingRuleId(10L)
                .finalPrice(BigDecimal.valueOf(500.00))
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        // When
        WaybillEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getWaybillNumber()).isEqualTo("WB-2024-000001");
        assertThat(entity.getWaybillCreatorId()).isEqualTo(100L);
        assertThat(entity.getSenderUserId()).isEqualTo(200L);
        assertThat(entity.getRecipientUserId()).isEqualTo(300L);
        assertThat(entity.getRecipientAddress()).isEqualTo("Москва, ул. Ленина, д. 1");
        assertThat(entity.getWeightActual()).isEqualByComparingTo(BigDecimal.valueOf(5.50));
        assertThat(entity.getDimensions()).isEqualTo(dimensions);
        assertThat(entity.getPricingRuleId()).isEqualTo(10L);
        assertThat(entity.getFinalPrice()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(entity.getStatus()).isEqualTo(WaybillStatus.ACCEPTED_AT_PVZ);
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getAcceptedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        Dimensions dimensions = Dimensions.of(
                BigDecimal.valueOf(20.00),
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(40.00)
        );

        WaybillEntity entity = WaybillEntity.builder()
                .id(2L)
                .waybillNumber("WB-2024-000002")
                .waybillCreatorId(101L)
                .senderUserId(201L)
                .recipientUserId(301L)
                .recipientAddress("Санкт-Петербург, пр. Невский, д. 20")
                .weightActual(BigDecimal.valueOf(3.25))
                .dimensions(dimensions)
                .pricingRuleId(11L)
                .finalPrice(BigDecimal.valueOf(350.00))
                .status(WaybillStatus.IN_TRANSIT)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        // When
        Waybill domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getWaybillNumber()).isEqualTo("WB-2024-000002");
        assertThat(domain.getWaybillCreatorId()).isEqualTo(101L);
        assertThat(domain.getSenderUserId()).isEqualTo(201L);
        assertThat(domain.getRecipientUserId()).isEqualTo(301L);
        assertThat(domain.getRecipientAddress()).isEqualTo("Санкт-Петербург, пр. Невский, д. 20");
        assertThat(domain.getWeightActual()).isEqualByComparingTo(BigDecimal.valueOf(3.25));
        assertThat(domain.getDimensions()).isEqualTo(dimensions);
        assertThat(domain.getPricingRuleId()).isEqualTo(11L);
        assertThat(domain.getFinalPrice()).isEqualByComparingTo(BigDecimal.valueOf(350.00));
        assertThat(domain.getStatus()).isEqualTo(WaybillStatus.IN_TRANSIT);
        assertThat(domain.getCreatedAt()).isNotNull();
        assertThat(domain.getAcceptedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать накладную без габаритов")
    void shouldMapWaybillWithoutDimensions() {
        // Given
        Waybill domain = Waybill.builder()
                .id(3L)
                .waybillNumber("WB-2024-000003")
                .waybillCreatorId(102L)
                .senderUserId(202L)
                .recipientUserId(302L)
                .recipientAddress("Казань, ул. Пушкина, д. 5")
                .weightActual(BigDecimal.valueOf(1.00))
                .dimensions(null)
                .pricingRuleId(12L)
                .finalPrice(BigDecimal.valueOf(200.00))
                .status(WaybillStatus.DELIVERED)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        // When
        WaybillEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getDimensions()).isNull();
        assertThat(entity.getWeightActual()).isEqualByComparingTo(BigDecimal.valueOf(1.00));
    }

    @Test
    @DisplayName("Должен корректно обрабатывать все статусы накладной")
    void shouldMapAllWaybillStatuses() {
        // Given
        Waybill domain = Waybill.builder()
                .id(4L)
                .waybillNumber("WB-2024-000004")
                .waybillCreatorId(103L)
                .senderUserId(203L)
                .recipientUserId(303L)
                .recipientAddress("Екатеринбург, ул. Ленина, д. 10")
                .weightActual(BigDecimal.valueOf(2.50))
                .finalPrice(BigDecimal.valueOf(300.00))
                .status(WaybillStatus.CANCELLED)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        // When
        WaybillEntity entity = mapper.toEntity(domain);
        Waybill mappedBack = mapper.toDomain(entity);

        // Then
        assertThat(mappedBack.getStatus()).isEqualTo(WaybillStatus.CANCELLED);
    }
}
