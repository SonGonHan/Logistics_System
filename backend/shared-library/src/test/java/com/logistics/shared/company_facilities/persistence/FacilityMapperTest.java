package com.logistics.shared.company_facilities.persistence;

import com.logistics.shared.company_facilities.domain.Facility;
import com.logistics.shared.company_facilities.domain.FacilityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FacilityMapper: юнит-тесты")
class FacilityMapperTest {

    private FacilityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FacilityMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(10);
        FacilityEntity entity = FacilityEntity.builder()
                .id(1L)
                .type(FacilityType.PVZ)
                .name("ПВЗ Центр")
                .address("г. Москва, ул. Центральная, 1")
                .latitude(new BigDecimal("55.7558700"))
                .longitude(new BigDecimal("37.6173000"))
                .createdAt(createdAt)
                .closedDate(null)
                .build();

        // When
        Facility domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getType()).isEqualTo(FacilityType.PVZ);
        assertThat(domain.getName()).isEqualTo("ПВЗ Центр");
        assertThat(domain.getAddress()).isEqualTo("г. Москва, ул. Центральная, 1");
        assertThat(domain.getLatitude()).isEqualByComparingTo(new BigDecimal("55.7558700"));
        assertThat(domain.getLongitude()).isEqualByComparingTo(new BigDecimal("37.6173000"));
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        assertThat(domain.getClosedDate()).isNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        LocalDate closedDate = LocalDate.now().plusMonths(6);
        Facility domain = Facility.builder()
                .id(2L)
                .type(FacilityType.WAREHOUSE)
                .name("Склад Север-1")
                .address("г. Москва, ул. Складская, 5")
                .latitude(new BigDecimal("55.8000000"))
                .longitude(new BigDecimal("37.6500000"))
                .createdAt(LocalDateTime.now().minusYears(1))
                .closedDate(closedDate)
                .build();

        // When
        FacilityEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getType()).isEqualTo(FacilityType.WAREHOUSE);
        assertThat(entity.getName()).isEqualTo("Склад Север-1");
        assertThat(entity.getAddress()).isEqualTo("г. Москва, ул. Складская, 5");
        assertThat(entity.getLatitude()).isEqualByComparingTo(new BigDecimal("55.8000000"));
        assertThat(entity.getLongitude()).isEqualByComparingTo(new BigDecimal("37.6500000"));
        assertThat(entity.getClosedDate()).isEqualTo(closedDate);
    }

    @Test
    @DisplayName("Должен корректно обработать null-поля при преобразовании Entity в Domain")
    void shouldMapEntityWithNullOptionalFields() {
        // Given
        FacilityEntity entity = FacilityEntity.builder()
                .id(3L)
                .type(FacilityType.OFFICE)
                .name("Офис")
                .address("г. Москва, Тверская, 10")
                .latitude(null)
                .longitude(null)
                .createdAt(null)
                .closedDate(null)
                .build();

        // When
        Facility domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getLatitude()).isNull();
        assertThat(domain.getLongitude()).isNull();
        assertThat(domain.getCreatedAt()).isNull();
        assertThat(domain.getClosedDate()).isNull();
    }
}