package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import com.logistics.corebusiness.waybill.domain.WaybillStatusHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WaybillStatusHistoryPersistenceMapper: юнит-тесты")
class WaybillStatusHistoryPersistenceMapperTest {

    private WaybillStatusHistoryPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WaybillStatusHistoryPersistenceMapper();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Domain в Entity")
    void shouldMapDomainToEntity() {
        // Given
        WaybillStatusHistory domain = WaybillStatusHistory.builder()
                .id(1L)
                .waybillId(100L)
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .facilityId(10L)
                .notes("Посылка принята на ПВЗ №10")
                .changedBy(500L)
                .changedAt(LocalDateTime.now())
                .build();

        // When
        WaybillStatusHistoryEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getWaybillId()).isEqualTo(100L);
        assertThat(entity.getStatus()).isEqualTo(WaybillStatus.ACCEPTED_AT_PVZ);
        assertThat(entity.getFacilityId()).isEqualTo(10L);
        assertThat(entity.getNotes()).isEqualTo("Посылка принята на ПВЗ №10");
        assertThat(entity.getChangedBy()).isEqualTo(500L);
        assertThat(entity.getChangedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать Entity в Domain")
    void shouldMapEntityToDomain() {
        // Given
        WaybillStatusHistoryEntity entity = WaybillStatusHistoryEntity.builder()
                .id(2L)
                .waybillId(101L)
                .status(WaybillStatus.IN_TRANSIT)
                .facilityId(11L)
                .notes("Посылка в пути")
                .changedBy(501L)
                .changedAt(LocalDateTime.now())
                .build();

        // When
        WaybillStatusHistory domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getWaybillId()).isEqualTo(101L);
        assertThat(domain.getStatus()).isEqualTo(WaybillStatus.IN_TRANSIT);
        assertThat(domain.getFacilityId()).isEqualTo(11L);
        assertThat(domain.getNotes()).isEqualTo("Посылка в пути");
        assertThat(domain.getChangedBy()).isEqualTo(501L);
        assertThat(domain.getChangedAt()).isNotNull();
    }

    @Test
    @DisplayName("Должен корректно преобразовать запись с null значениями")
    void shouldMapHistoryWithNullValues() {
        // Given
        WaybillStatusHistory domain = WaybillStatusHistory.builder()
                .id(3L)
                .waybillId(102L)
                .status(WaybillStatus.DELIVERED)
                .facilityId(null)
                .notes(null)
                .changedBy(null)
                .changedAt(LocalDateTime.now())
                .build();

        // When
        WaybillStatusHistoryEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getFacilityId()).isNull();
        assertThat(entity.getNotes()).isNull();
        assertThat(entity.getChangedBy()).isNull();
        assertThat(entity.getStatus()).isEqualTo(WaybillStatus.DELIVERED);
    }

    @Test
    @DisplayName("Должен корректно обрабатывать все статусы накладной")
    void shouldMapAllWaybillStatuses() {
        for (WaybillStatus status : WaybillStatus.values()) {
            // Given
            WaybillStatusHistory domain = WaybillStatusHistory.builder()
                    .id(1L)
                    .waybillId(100L)
                    .status(status)
                    .facilityId(10L)
                    .notes("Test note for " + status)
                    .changedBy(500L)
                    .changedAt(LocalDateTime.now())
                    .build();

            // When
            WaybillStatusHistoryEntity entity = mapper.toEntity(domain);
            WaybillStatusHistory mappedBack = mapper.toDomain(entity);

            // Then
            assertThat(mappedBack.getStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Должен сохранять длинные заметки")
    void shouldMapLongNotes() {
        // Given
        String longNotes = "Очень длинная заметка ".repeat(50);
        WaybillStatusHistory domain = WaybillStatusHistory.builder()
                .id(4L)
                .waybillId(103L)
                .status(WaybillStatus.CANCELLED)
                .facilityId(12L)
                .notes(longNotes)
                .changedBy(502L)
                .changedAt(LocalDateTime.now())
                .build();

        // When
        WaybillStatusHistoryEntity entity = mapper.toEntity(domain);
        WaybillStatusHistory mappedBack = mapper.toDomain(entity);

        // Then
        assertThat(mappedBack.getNotes()).isEqualTo(longNotes);
    }
}
