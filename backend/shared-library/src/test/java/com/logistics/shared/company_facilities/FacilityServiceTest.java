package com.logistics.shared.company_facilities;

import com.logistics.shared.company_facilities.domain.Facility;
import com.logistics.shared.company_facilities.domain.FacilityType;
import com.logistics.shared.company_facilities.persistence.FacilityEntity;
import com.logistics.shared.company_facilities.persistence.FacilityJpaRepository;
import com.logistics.shared.company_facilities.persistence.FacilityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityService: юнит-тесты")
class FacilityServiceTest {

    @Mock
    private FacilityJpaRepository repository;
    @Mock
    private FacilityMapper mapper;
    @InjectMocks
    private FacilityService service;

    private FacilityEntity testEntity;
    private Facility activeFacility;

    @BeforeEach
    void setUp() {
        testEntity = FacilityEntity.builder()
                .id(1L)
                .type(FacilityType.PVZ)
                .name("ПВЗ Центр")
                .address("г. Москва, ул. Центральная, 1")
                .build();

        activeFacility = Facility.builder()
                .id(1L)
                .type(FacilityType.PVZ)
                .name("ПВЗ Центр")
                .closedDate(null)
                .build();
    }

    @Test
    @DisplayName("Должен вернуть все объекты компании")
    void shouldReturnAllFacilities() {
        // Given
        when(repository.findAll()).thenReturn(List.of(testEntity));
        when(mapper.toDomain(any(FacilityEntity.class))).thenReturn(activeFacility);

        // When
        List<Facility> result = service.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Должен вернуть объекты по типу, включая закрытые")
    void shouldReturnFacilitiesByType() {
        // Given
        when(repository.findAllByType(FacilityType.PVZ)).thenReturn(List.of(testEntity));
        when(mapper.toDomain(any(FacilityEntity.class))).thenReturn(activeFacility);

        // When
        List<Facility> result = service.getByType(FacilityType.PVZ);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(FacilityType.PVZ);
        verify(repository).findAllByType(FacilityType.PVZ);
    }

    @Test
    @DisplayName("Должен вернуть пустой список, если объектов данного типа нет")
    void shouldReturnEmptyListWhenNoFacilitiesOfType() {
        // Given
        when(repository.findAllByType(FacilityType.WAREHOUSE)).thenReturn(List.of());

        // When
        List<Facility> result = service.getByType(FacilityType.WAREHOUSE);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть объект по ID")
    void shouldReturnFacilityById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(activeFacility);

        // When
        Optional<Facility> result = service.getById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, если объект не найден по ID")
    void shouldReturnEmptyWhenFacilityNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Facility> result = service.getById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getActiveByType должен отфильтровать закрытые объекты")
    void shouldReturnOnlyActiveFacilitiesByType() {
        // Given
        Facility closedFacility = Facility.builder()
                .id(2L)
                .type(FacilityType.PVZ)
                .closedDate(LocalDate.now().minusDays(1))
                .build();

        when(repository.findAllByType(FacilityType.PVZ))
                .thenReturn(List.of(testEntity, testEntity));
        when(mapper.toDomain(any(FacilityEntity.class)))
                .thenReturn(activeFacility)
                .thenReturn(closedFacility);

        // When
        List<Facility> result = service.getActiveByType(FacilityType.PVZ);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    // TODO(human): протестируй граничные условия метода isActive().
    // Реализуй 3 теста, вызывая service.isActive(facility) напрямую.
    // Проверь три случая: closedDate = null, closedDate = сегодня, closedDate = завтра.
    // Подсказка: isAfter() — строгое сравнение. Подумай, что вернёт isActive,
    // если дата закрытия совпадает с сегодняшним днём.

    @Test
    @DisplayName("isActive должен вернуть true, если объект не закрывается")
    void shouldReturnTrueWhenActiveFacilityIsActive() {
        // Given
        Facility activeFacility = Facility.builder()
                .id(2L)
                .type(FacilityType.PVZ)
                .closedDate(null)
                .build();

        //When
        boolean result = service.isActive(activeFacility);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isActive должен вернуть true, если объект закрывается завтра")
    void shouldReturnFalseWhenActiveFacilityIsClosedTomorrow() {
        // Given
        Facility activeFacility = Facility.builder()
                .id(2L)
                .type(FacilityType.PVZ)
                .closedDate(LocalDate.now().plusDays(1))
                .build();

        //When
        boolean result = service.isActive(activeFacility);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isActive должен вернуть false, если объект закрывается сегодня")
    void shouldReturnFalseWhenActiveFacilityIsClosedToday() {
        // Given
        Facility activeFacility = Facility.builder()
                .id(2L)
                .type(FacilityType.PVZ)
                .closedDate(LocalDate.now())
                .build();

        //When
        boolean result = service.isActive(activeFacility);

        // Then
        assertThat(result).isFalse();
    }
}