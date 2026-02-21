package com.logistics.corebusiness.waybill.adapter.out.persistence.draft;

import com.logistics.corebusiness.IntegrationTest;
import com.logistics.corebusiness.waybill.domain.Dimensions;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
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
@DisplayName("DraftJpaRepository: интеграционные тесты")
class DraftJpaRepositoryIntegrationTest {

    @Autowired
    private DraftJpaRepository repository;

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
    @DisplayName("Должен сохранить и найти Draft по ID")
    void shouldSaveAndFindById() {
        // Given
        Dimensions dimensions = Dimensions.of(
                BigDecimal.valueOf(25.00),
                BigDecimal.valueOf(35.00),
                BigDecimal.valueOf(45.00)
        );

        DraftEntity entity = DraftEntity.builder()
                .barcode("BC-TEST-001")
                .draftCreatorId(1L)
                .senderUserId(11L)
                .recipientUserId(21L)
                .recipientAddress("Москва, ул. Тестовая, д. 1")
                .weightDeclared(BigDecimal.valueOf(4.50))
                .dimensions(dimensions)
                .pricingRuleId(10L)
                .estimatedPrice(BigDecimal.valueOf(450.00))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DraftEntity saved = repository.save(entity);
        Optional<DraftEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getBarcode()).isEqualTo("BC-TEST-001");
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getWeightDeclared()).isEqualByComparingTo(BigDecimal.valueOf(4.50));
        assertThat(found.get().getDimensions()).isEqualTo(dimensions);
    }

    @Test
    @DisplayName("Должен найти Draft по штрих-коду")
    void shouldFindByBarcode() {
        // Given
        DraftEntity entity = DraftEntity.builder()
                .barcode("BC-UNIQUE-123")
                .draftCreatorId(2L)
                .senderUserId(12L)
                .recipientUserId(22L)
                .recipientAddress("Санкт-Петербург, пр. Невский, д. 20")
                .weightDeclared(BigDecimal.valueOf(2.75))
                .estimatedPrice(BigDecimal.valueOf(300.00))
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(entity);

        // When
        Optional<DraftEntity> found = repository.findByBarcode("BC-UNIQUE-123");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getBarcode()).isEqualTo("BC-UNIQUE-123");
        assertThat(found.get().getDraftStatus()).isEqualTo(DraftStatus.PENDING);
    }

    @Test
    @DisplayName("Должен найти все Draft по senderId")
    void shouldFindBySenderUserId() {
        // Given
        Long senderId = 12L;

        DraftEntity entity1 = createDraftEntity("BC-SENDER-001", senderId, 21L, DraftStatus.PENDING);
        DraftEntity entity2 = createDraftEntity("BC-SENDER-002", senderId, 22L, DraftStatus.CONFIRMED);
        DraftEntity entity3 = createDraftEntity("BC-SENDER-003", 11L, 22L, DraftStatus.CANCELLED);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<DraftEntity> found = repository.findBySenderUserId(senderId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(DraftEntity::getBarcode)
                .containsExactlyInAnyOrder("BC-SENDER-001", "BC-SENDER-002");
    }

    @Test
    @DisplayName("Должен найти все Draft по recipientId")
    void shouldFindByRecipientUserId() {
        // Given
        Long recipientId = 22L;

        DraftEntity entity1 = createDraftEntity("BC-RECIP-001", 11L, recipientId, DraftStatus.PENDING);
        DraftEntity entity2 = createDraftEntity("BC-RECIP-002", 12L, recipientId, DraftStatus.PENDING);
        DraftEntity entity3 = createDraftEntity("BC-RECIP-003", 2L, 21L, DraftStatus.CONFIRMED);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<DraftEntity> found = repository.findByRecipientUserId(recipientId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(DraftEntity::getBarcode)
                .containsExactlyInAnyOrder("BC-RECIP-001", "BC-RECIP-002");
    }

    @Test
    @DisplayName("Должен найти все Draft по статусу")
    void shouldFindByDraftStatus() {
        // Given
        DraftEntity entity1 = createDraftEntity("BC-STATUS-001", 11L, 21L, DraftStatus.PENDING);
        DraftEntity entity2 = createDraftEntity("BC-STATUS-002", 12L, 22L, DraftStatus.PENDING);
        DraftEntity entity3 = createDraftEntity("BC-STATUS-003", 11L, 21L, DraftStatus.CONFIRMED);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<DraftEntity> found = repository.findByDraftStatus(DraftStatus.PENDING);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(DraftEntity::getBarcode)
                .containsExactlyInAnyOrder("BC-STATUS-001", "BC-STATUS-002");
    }

    @Test
    @DisplayName("Должен найти все Draft по создателю")
    void shouldFindByDraftCreatorId() {
        // Given
        Long creatorId = 2L;

        DraftEntity entity1 = createDraftEntity("BC-CREATOR-001", 11L, 21L, DraftStatus.PENDING);
        entity1.setDraftCreatorId(creatorId);

        DraftEntity entity2 = createDraftEntity("BC-CREATOR-002", 12L, 22L, DraftStatus.CONFIRMED);
        entity2.setDraftCreatorId(creatorId);

        DraftEntity entity3 = createDraftEntity("BC-CREATOR-003", 11L, 21L, DraftStatus.CANCELLED);
        entity3.setDraftCreatorId(1L);

        repository.saveAll(List.of(entity1, entity2, entity3));

        // When
        List<DraftEntity> found = repository.findByDraftCreatorId(creatorId);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(DraftEntity::getBarcode)
                .containsExactlyInAnyOrder("BC-CREATOR-001", "BC-CREATOR-002");
    }

    @Test
    @DisplayName("Должен корректно сохранять черновик с null значениями")
    void shouldSaveDraftWithNullValues() {
        // Given
        DraftEntity entity = DraftEntity.builder()
                .barcode("BC-NULL-VALUES-001")
                .draftCreatorId(1L)
                .senderUserId(11L)
                .recipientUserId(21L)
                .recipientAddress("Test Address")
                .weightDeclared(null)
                .dimensions(null)
                .pricingRuleId(null)
                .estimatedPrice(null)
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        DraftEntity saved = repository.save(entity);
        Optional<DraftEntity> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getWeightDeclared()).isNull();
        assertThat(found.get().getDimensions()).isNull();
        assertThat(found.get().getPricingRuleId()).isNull();
        assertThat(found.get().getEstimatedPrice()).isNull();
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional для несуществующего штрих-кода")
    void shouldReturnEmptyOptionalForNonExistentBarcode() {
        // When
        Optional<DraftEntity> found = repository.findByBarcode("NON-EXISTENT");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть пустой список для senderId без черновиков")
    void shouldReturnEmptyListForSenderWithNoDrafts() {
        // When
        List<DraftEntity> found = repository.findBySenderUserId(99999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен корректно обрабатывать все статусы черновика")
    void shouldHandleAllDraftStatuses() {
        // Given
        DraftEntity pendingDraft = createDraftEntity("BC-PENDING", 11L, 21L, DraftStatus.PENDING);
        DraftEntity confirmedDraft = createDraftEntity("BC-CONFIRMED", 12L, 22L, DraftStatus.CONFIRMED);
        DraftEntity cancelledDraft = createDraftEntity("BC-CANCELLED", 11L, 21L, DraftStatus.CANCELLED);

        repository.saveAll(List.of(pendingDraft, confirmedDraft, cancelledDraft));

        // When & Then
        assertThat(repository.findByDraftStatus(DraftStatus.PENDING)).hasSize(1);
        assertThat(repository.findByDraftStatus(DraftStatus.CONFIRMED)).hasSize(1);
        assertThat(repository.findByDraftStatus(DraftStatus.CANCELLED)).hasSize(1);
    }

    private DraftEntity createDraftEntity(String barcode, Long senderId, Long recipientId, DraftStatus status) {
        return DraftEntity.builder()
                .barcode(barcode)
                .draftCreatorId(1L) // Default creator
                .senderUserId(senderId)
                .recipientUserId(recipientId)
                .recipientAddress("Test Address")
                .weightDeclared(BigDecimal.valueOf(2.00))
                .estimatedPrice(BigDecimal.valueOf(250.00))
                .draftStatus(status)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
