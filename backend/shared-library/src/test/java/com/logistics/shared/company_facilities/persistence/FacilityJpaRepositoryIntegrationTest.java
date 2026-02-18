package com.logistics.shared.company_facilities.persistence;

import com.logistics.shared.IntegrationTest;
import com.logistics.shared.company_facilities.domain.FacilityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("FacilityJpaRepository: интеграционные тесты")
class FacilityJpaRepositoryIntegrationTest {

    @Autowired
    private FacilityJpaRepository repository;

    @Test
    @DisplayName("Должен сохранить и найти объект по ID")
    void shouldSaveAndFindById() {
        // Given
        FacilityEntity entity = FacilityEntity.builder()
                .type(FacilityType.PVZ)
                .name("ПВЗ Тест")
                .address("г. Москва, ул. Тестовая, 1")
                .latitude(new BigDecimal("55.7500000"))
                .longitude(new BigDecimal("37.6100000"))
                .createdAt(LocalDateTime.now())
                .build();

        FacilityEntity saved = repository.save(entity);

        // When
        Optional<FacilityEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ПВЗ Тест");
        assertThat(found.get().getType()).isEqualTo(FacilityType.PVZ);
        assertThat(found.get().getClosedDate()).isNull();
    }

    @Test
    @DisplayName("Должен вернуть только объекты нужного типа через findAllByType")
    void shouldFindAllByType() {
        // Given
        FacilityEntity pvz = FacilityEntity.builder()
                .type(FacilityType.PVZ)
                .name("ПВЗ-1")
                .address("Адрес ПВЗ")
                .createdAt(LocalDateTime.now())
                .build();

        FacilityEntity warehouse = FacilityEntity.builder()
                .type(FacilityType.WAREHOUSE)
                .name("Склад-1")
                .address("Адрес склада")
                .createdAt(LocalDateTime.now())
                .build();

        FacilityEntity office = FacilityEntity.builder()
                .type(FacilityType.OFFICE)
                .name("Офис-1")
                .address("Адрес офиса")
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(pvz);
        repository.save(warehouse);
        repository.save(office);

        // When
        List<FacilityEntity> pvzList = repository.findAllByType(FacilityType.PVZ);

        // Then
        assertThat(pvzList).hasSize(1);
        assertThat(pvzList.get(0).getType()).isEqualTo(FacilityType.PVZ);
        assertThat(pvzList.get(0).getName()).isEqualTo("ПВЗ-1");
    }

    @Test
    @DisplayName("Должен сохранить объект с датой закрытия")
    void shouldSaveFacilityWithClosedDate() {
        // Given
        LocalDate closedDate = LocalDate.now().minusDays(5);
        FacilityEntity entity = FacilityEntity.builder()
                .type(FacilityType.WAREHOUSE)
                .name("Закрытый склад")
                .address("г. Москва, ул. Старая, 10")
                .createdAt(LocalDateTime.now().minusYears(2))
                .closedDate(closedDate)
                .build();

        // When
        FacilityEntity saved = repository.save(entity);
        Optional<FacilityEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getClosedDate()).isEqualTo(closedDate);
    }
}