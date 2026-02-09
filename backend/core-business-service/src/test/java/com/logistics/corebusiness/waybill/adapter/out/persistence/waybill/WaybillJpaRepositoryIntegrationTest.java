package com.logistics.corebusiness.waybill.adapter.out.persistence.waybill;

import com.logistics.corebusiness.IntegrationTest;
import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@DisplayName("WaybillJpaRepository: интеграционные тесты")
class WaybillJpaRepositoryIntegrationTest {

    @Autowired
    private WaybillJpaRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Creator: 1, 2 | Sender: 11, 12 | Recipient: 21, 22
        List<Long> userIds = List.of(1L, 2L, 11L, 12L, 21L, 22L);
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
    }

    @Test
    @DisplayName("Должен сохранить и найти Waybill по ID")
    void shouldSaveAndFindById() {
        // Given
        Dimensions dimensions = Dimensions.of(
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(40.00),
                BigDecimal.valueOf(50.00)
        );

        WaybillEntity entity = WaybillEntity.builder()
                .waybillNumber("WB-TEST-001")
                .waybillCreatorId(1L)
                .senderUserId(11L)
                .recipientUserId(21L)
                .recipientAddress("Москва, ул. Тестовая, д. 1")
                .weightActual(BigDecimal.valueOf(5.50))
                .dimensions(dimensions)
                .pricingRuleId(10L)
                .finalPrice(BigDecimal.valueOf(500.00))
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        // When
        WaybillEntity saved = repository.save(entity);
        Optional<WaybillEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getWaybillNumber()).isEqualTo("WB-TEST-001");
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getWeightActual()).isEqualByComparingTo(BigDecimal.valueOf(5.50));
        assertThat(found.get().getDimensions()).isEqualTo(dimensions);
    }

    @Test
    @DisplayName("Должен найти Waybill по номеру накладной")
    void shouldFindByWaybillNumber() {
        // Given
        WaybillEntity entity = WaybillEntity.builder()
                .waybillNumber("WB-UNIQUE-123")
                .waybillCreatorId(2L)
                .senderUserId(12L)
                .recipientUserId(22L)
                .recipientAddress("Санкт-Петербург, пр. Невский, д. 20")
                .weightActual(BigDecimal.valueOf(3.25))
                .finalPrice(BigDecimal.valueOf(350.00))
                .status(WaybillStatus.IN_TRANSIT)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        repository.save(entity);

        // When
        Optional<WaybillEntity> found = repository.findByWaybillNumber("WB-UNIQUE-123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getWaybillNumber()).isEqualTo("WB-UNIQUE-123");
        assertThat(found.get().getStatus()).isEqualTo(WaybillStatus.IN_TRANSIT);
    }

    @Test
    @DisplayName("Должен найти все Waybill по senderId")
    void shouldFindBySenderUserId() {
        // Given
        Long senderId = 12L;

        WaybillEntity entity1 = createWaybillEntity("WB-SENDER-001", senderId, 21L, WaybillStatus.ACCEPTED_AT_PVZ);
        WaybillEntity entity2 = createWaybillEntity("WB-SENDER-002", senderId, 22L, WaybillStatus.IN_TRANSIT);
        WaybillEntity entity3 = createWaybillEntity("WB-SENDER-003", 11L, 22L, WaybillStatus.DELIVERED);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<WaybillEntity> found = repository.findBySenderUserId(senderId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(WaybillEntity::getWaybillNumber)
                .containsExactlyInAnyOrder("WB-SENDER-001", "WB-SENDER-002");
    }

    @Test
    @DisplayName("Должен найти все Waybill по recipientId")
    void shouldFindByRecipientUserId() {
        // Given
        Long recipientId = 22L;

        WaybillEntity entity1 = createWaybillEntity("WB-RECIP-001", 11L, recipientId, WaybillStatus.OUT_FOR_DELIVERY);
        WaybillEntity entity2 = createWaybillEntity("WB-RECIP-002", 12L, recipientId, WaybillStatus.READY_FOR_PICKUP);
        WaybillEntity entity3 = createWaybillEntity("WB-RECIP-003", 2L, 21L, WaybillStatus.DELIVERED);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<WaybillEntity> found = repository.findByRecipientUserId(recipientId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(WaybillEntity::getWaybillNumber)
                .containsExactlyInAnyOrder("WB-RECIP-001", "WB-RECIP-002");
    }

    @Test
    @DisplayName("Должен найти все Waybill по статусу")
    void shouldFindByStatus() {
        // Given
        WaybillEntity entity1 = createWaybillEntity("WB-STATUS-001", 11L, 21L, WaybillStatus.DELIVERED);
        WaybillEntity entity2 = createWaybillEntity("WB-STATUS-002", 12L, 22L, WaybillStatus.DELIVERED);
        WaybillEntity entity3 = createWaybillEntity("WB-STATUS-003", 11L, 21L, WaybillStatus.CANCELLED);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<WaybillEntity> found = repository.findByStatus(WaybillStatus.DELIVERED);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(WaybillEntity::getWaybillNumber)
                .containsExactlyInAnyOrder("WB-STATUS-001", "WB-STATUS-002");
    }

    @Test
    @DisplayName("Должен найти все Waybill по создателю")
    void shouldFindByWaybillCreatorId() {
        // Given
        Long creatorId = 2L;

        WaybillEntity entity1 = createWaybillEntity("WB-CREATOR-001", 11L, 21L, WaybillStatus.ACCEPTED_AT_PVZ);
        entity1.setWaybillCreatorId(creatorId);

        WaybillEntity entity2 = createWaybillEntity("WB-CREATOR-002", 12L, 22L, WaybillStatus.IN_TRANSIT);
        entity2.setWaybillCreatorId(creatorId);

        WaybillEntity entity3 = createWaybillEntity("WB-CREATOR-003", 11L, 21L, WaybillStatus.DELIVERED);
        entity3.setWaybillCreatorId(1L);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<WaybillEntity> found = repository.findByWaybillCreatorId(creatorId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(WaybillEntity::getWaybillNumber)
                .containsExactlyInAnyOrder("WB-CREATOR-001", "WB-CREATOR-002");
    }

    @Test
    @DisplayName("Должен корректно сохранять накладную без габаритов")
    void shouldSaveWaybillWithoutDimensions() {
        // Given
        WaybillEntity entity = WaybillEntity.builder()
                .waybillNumber("WB-NO-DIM-001")
                .waybillCreatorId(1L)
                .senderUserId(11L)
                .recipientUserId(21L)
                .recipientAddress("Казань, ул. Пушкина, д. 5")
                .weightActual(BigDecimal.valueOf(1.00))
                .dimensions(null)
                .finalPrice(BigDecimal.valueOf(200.00))
                .status(WaybillStatus.ACCEPTED_AT_PVZ)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        // When
        WaybillEntity saved = repository.save(entity);
        Optional<WaybillEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDimensions()).isNull();
        assertThat(found.get().getWeightActual()).isEqualByComparingTo(BigDecimal.valueOf(1.00));
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional для несуществующего номера накладной")
    void shouldReturnEmptyOptionalForNonExistentWaybillNumber() {
        // When
        Optional<WaybillEntity> found = repository.findByWaybillNumber("NON-EXISTENT");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть пустой список для senderId без накладных")
    void shouldReturnEmptyListForSenderWithNoWaybills() {
        // When
        List<WaybillEntity> found = repository.findBySenderUserId(99999L);

        // Then
        assertThat(found).isEmpty();
    }

    private WaybillEntity createWaybillEntity(String waybillNumber, Long senderId, Long recipientId, WaybillStatus status) {
        return WaybillEntity.builder()
                .waybillNumber(waybillNumber)
                .waybillCreatorId(1L) // Default creator
                .senderUserId(senderId)
                .recipientUserId(recipientId)
                .recipientAddress("Test Address")
                .weightActual(BigDecimal.valueOf(2.50))
                .finalPrice(BigDecimal.valueOf(300.00))
                .status(status)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();
    }
}
