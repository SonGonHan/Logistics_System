package com.logistics.corebusiness.waybill.adapter.out.persistence.history;

import com.logistics.corebusiness.IntegrationTest;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("WaybillStatusHistoryJpaRepository: интеграционные тесты")
class WaybillStatusHistoryJpaRepositoryIntegrationTest {

    @Autowired
    private WaybillStatusHistoryJpaRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Creator: 1, 2 | ChangedBy: 11, 12
        List<Long> userIds = List.of(1L, 2L, 11L, 12L);
        for (Long userId : userIds) {
            jdbcTemplate.update("""
                INSERT INTO user_management.users
                (user_id, email, role_name)
                VALUES (?, ?, ?)
                """,
                userId,
                "user" + userId + "@test.com",
                "CLIENT"
            );
        }

        List<Long> waybillIds = List.of(1L, 2L, 3L, 4L);
        for  (Long waybillId : waybillIds) {
            jdbcTemplate.update("""
                    INSERT INTO waybill_service.waybills
                    (waybill_id, waybill_number, waybill_creator_id, sender_user_id, recipient_user_id, recipient_address, weight_actual, final_price)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                waybillId,
                "waybill-num" + waybillId,
                userIds.getFirst(),
                userIds.getFirst(),
                userIds.getLast(),
                "address",
                2,
                2);
        }

        jdbcTemplate.update("""
                INSERT INTO shared_data.company_facilities
                (facility_id, facility_type, facility_name, address)
                VALUES (1, 'PVZ', 'name', 'address')
            """);
    }

    @Test
    @DisplayName("Должен сохранить и найти WaybillStatusHistory по ID")
    void shouldSaveAndFindById() {
        // Given
        WaybillStatusHistoryEntity entity = WaybillStatusHistoryEntity.builder()
                .waybillId(1L)
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .facilityId(1L)
                .notes("Посылка принята на ПВЗ №1")
                .changedBy(11L)
                .changedAt(LocalDateTime.now())
                .build();

        // When
        WaybillStatusHistoryEntity saved = repository.save(entity);
        Optional<WaybillStatusHistoryEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getWaybillId()).isEqualTo(1L);
        assertThat(found.get().getStatus()).isEqualTo(WaybillStatus.ACCEPTED_AT_PVZ);
        assertThat(found.get().getFacilityId()).isEqualTo(1L);
        assertThat(found.get().getNotes()).isEqualTo("Посылка принята на ПВЗ №1");
        assertThat(found.get().getChangedBy()).isEqualTo(11L);
    }

    @Test
    @DisplayName("Должен найти все записи истории по waybillId, упорядоченные по времени")
    void shouldFindByWaybillIdOrderByChangedAtAsc() {
        // Given
        Long waybillId = 1L;
        LocalDateTime now = LocalDateTime.now();

        WaybillStatusHistoryEntity entry1 = createHistoryEntity(waybillId, WaybillStatus.ACCEPTED_AT_PVZ, now.minusHours(3));
        WaybillStatusHistoryEntity entry2 = createHistoryEntity(waybillId, WaybillStatus.IN_TRANSIT, now.minusHours(2));
        WaybillStatusHistoryEntity entry3 = createHistoryEntity(waybillId, WaybillStatus.AT_SORTING_CENTER, now.minusHours(1));
        WaybillStatusHistoryEntity entry4 = createHistoryEntity(2L, WaybillStatus.DELIVERED, now); // другая накладная

        repository.saveAll(List.of(entry1, entry2, entry3, entry4));

        // When
        List<WaybillStatusHistoryEntity> found = repository.findByWaybillIdOrderByChangedAtAsc(waybillId);

        // Then
        assertThat(found).hasSize(3);
        assertThat(found).extracting(WaybillStatusHistoryEntity::getStatus)
                .containsExactly(
                        WaybillStatus.ACCEPTED_AT_PVZ,
                        WaybillStatus.IN_TRANSIT,
                        WaybillStatus.AT_SORTING_CENTER
                );
    }

    @Test
    @DisplayName("Должен найти все записи по facilityId")
    void shouldFindByFacilityId() {
        // Given
        Long facilityId = 1L;

        WaybillStatusHistoryEntity entry1 = createHistoryEntity(1L, WaybillStatus.ACCEPTED_AT_PVZ, LocalDateTime.now());
        entry1.setFacilityId(facilityId);

        WaybillStatusHistoryEntity entry2 = createHistoryEntity(2L, WaybillStatus.IN_TRANSIT, LocalDateTime.now());
        entry2.setFacilityId(facilityId);

        WaybillStatusHistoryEntity entry3 = createHistoryEntity(3L, WaybillStatus.DELIVERED, LocalDateTime.now());
        entry3.setFacilityId(2L); // другой объект

        repository.saveAll(List.of(entry1, entry2, entry3));

        // When
        List<WaybillStatusHistoryEntity> found = repository.findByFacilityId(facilityId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(WaybillStatusHistoryEntity::getWaybillId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("Должен найти все записи по changedBy (пользователь)")
    void shouldFindByChangedBy() {
        // Given
        Long userId = 11L;

        WaybillStatusHistoryEntity entry1 = createHistoryEntity(1L, WaybillStatus.ACCEPTED_AT_PVZ, LocalDateTime.now());
        entry1.setChangedBy(userId);

        WaybillStatusHistoryEntity entry2 = createHistoryEntity(2L, WaybillStatus.OUT_FOR_DELIVERY, LocalDateTime.now());
        entry2.setChangedBy(userId);

        WaybillStatusHistoryEntity entry3 = createHistoryEntity(3L, WaybillStatus.DELIVERED, LocalDateTime.now());
        entry3.setChangedBy(12L); // другой пользователь

        repository.saveAll(List.of(entry1, entry2, entry3));

        // When
        List<WaybillStatusHistoryEntity> found = repository.findByChangedBy(userId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(WaybillStatusHistoryEntity::getWaybillId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("Должен корректно сохранять запись с null значениями")
    void shouldSaveHistoryWithNullValues() {
        // Given
        WaybillStatusHistoryEntity entity = WaybillStatusHistoryEntity.builder()
                .waybillId(1L)
                .status(WaybillStatus.CANCELLED)
                .facilityId(null)
                .notes(null)
                .changedBy(null)
                .changedAt(LocalDateTime.now())
                .build();

        // When
        WaybillStatusHistoryEntity saved = repository.save(entity);
        Optional<WaybillStatusHistoryEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFacilityId()).isNull();
        assertThat(found.get().getNotes()).isNull();
        assertThat(found.get().getChangedBy()).isNull();
        assertThat(found.get().getStatus()).isEqualTo(WaybillStatus.CANCELLED);
    }

    @Test
    @DisplayName("Должен вернуть пустой список для waybillId без истории")
    void shouldReturnEmptyListForWaybillWithoutHistory() {
        // When
        List<WaybillStatusHistoryEntity> found = repository.findByWaybillIdOrderByChangedAtAsc(99999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен сохранять длинные заметки")
    void shouldSaveLongNotes() {
        // Given
        String longNotes = "Очень длинная заметка ".repeat(50);
        WaybillStatusHistoryEntity entity = WaybillStatusHistoryEntity.builder()
                .waybillId(1L)
                .status(WaybillStatus.RETURNING)
                .facilityId(1L)
                .notes(longNotes)
                .changedBy(12L)
                .changedAt(LocalDateTime.now())
                .build();

        // When
        WaybillStatusHistoryEntity saved = repository.save(entity);
        Optional<WaybillStatusHistoryEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNotes()).isEqualTo(longNotes);
    }

    @Test
    @DisplayName("Должен корректно обрабатывать все статусы накладной")
    void shouldHandleAllWaybillStatuses() {
        // Given
        Long waybillId = 1L;

        for (WaybillStatus status : WaybillStatus.values()) {
            WaybillStatusHistoryEntity entity = createHistoryEntity(waybillId, status, LocalDateTime.now());
            repository.save(entity);
        }

        // When
        List<WaybillStatusHistoryEntity> found = repository.findByWaybillIdOrderByChangedAtAsc(waybillId);

        // Then
        assertThat(found).hasSize(WaybillStatus.values().length);
        assertThat(found).extracting(WaybillStatusHistoryEntity::getStatus)
                .containsAll(List.of(WaybillStatus.values()));
    }

    @Test
    @DisplayName("Должен сохранять множество записей для одной накладной")
    void shouldSaveMultipleEntriesForOneWaybill() {
        // Given
        Long waybillId = 1L;
        LocalDateTime now = LocalDateTime.now();

        WaybillStatusHistoryEntity entry1 = createHistoryEntity(waybillId, WaybillStatus.ACCEPTED_AT_PVZ, now);
        entry1.setNotes("Принята на ПВЗ");

        WaybillStatusHistoryEntity entry2 = createHistoryEntity(waybillId, WaybillStatus.IN_TRANSIT, now.plusHours(1));
        entry2.setNotes("Отправлена со склада");

        WaybillStatusHistoryEntity entry3 = createHistoryEntity(waybillId, WaybillStatus.AT_SORTING_CENTER, now.plusHours(2));
        entry3.setNotes("Прибыла на сортировочный центр");

        WaybillStatusHistoryEntity entry4 = createHistoryEntity(waybillId, WaybillStatus.OUT_FOR_DELIVERY, now.plusHours(3));
        entry4.setNotes("Передана курьеру");

        WaybillStatusHistoryEntity entry5 = createHistoryEntity(waybillId, WaybillStatus.DELIVERED, now.plusHours(4));
        entry5.setNotes("Доставлена получателю");

        repository.saveAll(List.of(entry1, entry2, entry3, entry4, entry5));

        // When
        List<WaybillStatusHistoryEntity> found = repository.findByWaybillIdOrderByChangedAtAsc(waybillId);

        // Then
        assertThat(found).hasSize(5);
        assertThat(found.get(0).getNotes()).isEqualTo("Принята на ПВЗ");
        assertThat(found.get(4).getNotes()).isEqualTo("Доставлена получателю");
        assertThat(found.get(4).getStatus()).isEqualTo(WaybillStatus.DELIVERED);
    }

    @Test
    @DisplayName("Должен вернуть пустой список для facilityId без записей")
    void shouldReturnEmptyListForFacilityWithoutHistory() {
        // When
        List<WaybillStatusHistoryEntity> found = repository.findByFacilityId(99999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть пустой список для пользователя без изменений")
    void shouldReturnEmptyListForUserWithoutChanges() {
        // When
        List<WaybillStatusHistoryEntity> found = repository.findByChangedBy(99999L);

        // Then
        assertThat(found).isEmpty();
    }

    private WaybillStatusHistoryEntity createHistoryEntity(Long waybillId, WaybillStatus status, LocalDateTime changedAt) {
        return WaybillStatusHistoryEntity.builder()
                .waybillId(waybillId)
                .status(status)
                .facilityId(1L)
                .notes("Test note for " + status)
                .changedBy(1L) // Default user
                .changedAt(changedAt)
                .build();
    }
}
