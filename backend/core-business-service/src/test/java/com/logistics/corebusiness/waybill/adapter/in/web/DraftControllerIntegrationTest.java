package com.logistics.corebusiness.waybill.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.corebusiness.IntegrationTest;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.CreateDraftRequest;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DimensionsDto;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.UpdateDraftRequest;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для DraftController.
 *
 * <h2>Что тестируем</h2>
 * - Полный путь HTTP → Controller → Service → Repository → DB
 * - Валидацию входных данных
 * - Обработку ошибок
 * - Персистенцию в БД
 *
 * <h2>Технологии</h2>
 * - MockMvc для имитации HTTP запросов
 * - Testcontainers PostgreSQL для реальной БД
 * - Mock JWT для аутентификации
 */
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DraftController - интеграционные тесты REST API")
class DraftControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DraftRepository draftRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Long TEST_USER_1 = 1L;
    private static final Long TEST_USER_2 = 2L;
    private static final Long TEST_USER_3 = 3L;
    private static final Long RECIPIENT_USER = 11L;
    private static final Long PRICING_RULE_ID = 1L;

    @BeforeEach
    void setUp() {
        // Создаем тестовых пользователей
        createTestUsers();
        // Создаем pricing rule для тестов
        createTestPricingRule();
    }

    // ===========================
    // POST /waybills/drafts
    // ===========================

    @Nested
    @DisplayName("POST /waybills/drafts - создание черновика")
    class CreateDraft {

        @Test
        @DisplayName("Должен успешно создать черновик с габаритами")
        void shouldCreateDraftWithDimensions() throws Exception {
            // Given
            var dimensions = DimensionsDto.builder()
                    .length(new BigDecimal("30.5"))
                    .width(new BigDecimal("40.2"))
                    .height(new BigDecimal("50.8"))
                    .build();

            var request = CreateDraftRequest.builder()
                    .recipientUserId(TEST_USER_2)
                    .recipientAddress("г. Москва, ул. Тестовая, д. 1, кв. 5")
                    .weightDeclared(new BigDecimal("2.5"))
                    .pricingRuleId(PRICING_RULE_ID)
                    .dimensions(dimensions)
                    .build();

            // When & Then
            mockMvc.perform(post("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated());

            // Проверяем сохранение в БД
            var savedDrafts = draftRepository.findByDraftCreatorId(TEST_USER_1);
            assertThat(savedDrafts).hasSize(1);

            var savedDraft = savedDrafts.get(0);
            assertThat(savedDraft.getRecipientUserId()).isEqualTo(TEST_USER_2);
            assertThat(savedDraft.getRecipientAddress()).isEqualTo("г. Москва, ул. Тестовая, д. 1, кв. 5");
            assertThat(savedDraft.getWeightDeclared()).isEqualByComparingTo("2.5");
            assertThat(savedDraft.getDimensions()).isNotNull();
            assertThat(savedDraft.getDimensions().length()).isEqualByComparingTo("30.5");
            assertThat(savedDraft.getBarcode()).isNotNull();
            assertThat(savedDraft.getDraftStatus()).isEqualTo(DraftStatus.PENDING);
            assertThat(savedDraft.getEstimatedPrice()).isNotNull();
        }

        @Test
        @DisplayName("Должен успешно создать черновик без габаритов")
        void shouldCreateDraftWithoutDimensions() throws Exception {
            // Given
            var request = CreateDraftRequest.builder()
                    .recipientUserId(TEST_USER_2)
                    .recipientAddress("г. Санкт-Петербург, Невский пр., д. 10")
                    .weightDeclared(new BigDecimal("1.0"))
                    .pricingRuleId(PRICING_RULE_ID)
                    .dimensions(null)
                    .build();

            // When & Then
            mockMvc.perform(post("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated());

            // Проверяем сохранение
            var savedDrafts = draftRepository.findByDraftCreatorId(TEST_USER_1);
            assertThat(savedDrafts).hasSize(1);
            assertThat(savedDrafts.get(0).getDimensions()).isNull();
        }

        @Test
        @DisplayName("Должен вернуть 400 при пустом адресе")
        void shouldReturn400WhenAddressIsEmpty() throws Exception {
            // Given
            var request = CreateDraftRequest.builder()
                    .recipientUserId(TEST_USER_2)
                    .recipientAddress("")
                    .weightDeclared(new BigDecimal("2.5"))
                    .pricingRuleId(PRICING_RULE_ID)
                    .dimensions(null)
                    .build();

            // When & Then
            mockMvc.perform(post("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.fields.recipientAddress").exists());
        }

        @Test
        @DisplayName("Должен вернуть 400 при отрицательном весе")
        void shouldReturn400WhenWeightIsNegative() throws Exception {
            // Given
            var request = CreateDraftRequest.builder()
                    .recipientUserId(TEST_USER_2)
                    .recipientAddress("г. Москва, ул. Тестовая, д. 1")
                    .weightDeclared(new BigDecimal("-1.0"))
                    .pricingRuleId(PRICING_RULE_ID)
                    .dimensions(null)
                    .build();

            // When & Then
            mockMvc.perform(post("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.fields.weightDeclared").exists());
        }

        @Test
        @DisplayName("Должен вернуть 400 при отсутствии обязательных полей")
        void shouldReturn400WhenMissingRequiredFields() throws Exception {
            // Given
            var request = CreateDraftRequest.builder()
                    .recipientUserId(null)
                    .recipientAddress(null)
                    .weightDeclared(null)
                    .pricingRuleId(PRICING_RULE_ID)
                    .dimensions(null)
                    .build();

            // When & Then
            mockMvc.perform(post("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.fields.recipientUserId").exists())
                    .andExpect(jsonPath("$.fields.recipientAddress").exists())
                    .andExpect(jsonPath("$.fields.weightDeclared").exists());
        }
    }

    // ===========================
    // GET /waybills/drafts
    // ===========================

    @Nested
    @DisplayName("GET /waybills/drafts - получение списка черновиков")
    class GetUserDraftList {

        @Test
        @DisplayName("Должен вернуть все черновики пользователя")
        void shouldReturnAllUserDrafts() throws Exception {
            // Given
            createDraft(TEST_USER_1, "DRF-001", DraftStatus.PENDING);
            createDraft(TEST_USER_1, "DRF-002", DraftStatus.CONFIRMED);
            createDraft(TEST_USER_1, "DRF-003", DraftStatus.CANCELLED);
            // Черновики другого пользователя
            createDraft(TEST_USER_2, "DRF-004", DraftStatus.PENDING);

            // When & Then
            mockMvc.perform(get("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[*].barcode",
                            containsInAnyOrder("DRF-001", "DRF-002", "DRF-003")));
        }

        @Test
        @DisplayName("Должен вернуть пустой список для пользователя без черновиков")
        void shouldReturnEmptyListForUserWithoutDrafts() throws Exception {
            // Given
            createDraft(TEST_USER_2, "DRF-001", DraftStatus.PENDING);

            // When & Then
            mockMvc.perform(get("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Должен отфильтровать по статусу PENDING")
        void shouldFilterByPendingStatus() throws Exception {
            // Given
            createDraft(TEST_USER_1, "DRF-001", DraftStatus.PENDING);
            createDraft(TEST_USER_1, "DRF-002", DraftStatus.CONFIRMED);
            createDraft(TEST_USER_1, "DRF-003", DraftStatus.PENDING);

            // When & Then
            mockMvc.perform(get("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .param("status", "PENDING"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].draftStatus", everyItem(is("PENDING"))));
        }

        @Test
        @DisplayName("Должен отсортировать по дате создания (newest first)")
        void shouldSortByCreatedAtDesc() throws Exception {
            // Given - создаем с задержкой для разных timestamp
            createDraft(TEST_USER_1, "DRF-OLD", DraftStatus.PENDING);
            Thread.sleep(100);
            createDraft(TEST_USER_1, "DRF-NEW", DraftStatus.PENDING);

            // When & Then
            mockMvc.perform(get("/waybills/drafts")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].barcode").value("DRF-NEW"))
                    .andExpect(jsonPath("$[1].barcode").value("DRF-OLD"));
        }
    }

    // ===========================
    // GET /waybills/drafts/{id}
    // ===========================

    @Nested
    @DisplayName("GET /waybills/drafts/{id} - получение черновика по ID")
    class GetDraftById {

        @Test
        @DisplayName("Должен вернуть черновик по ID")
        void shouldReturnDraftById() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-123", DraftStatus.PENDING);

            // When & Then
            mockMvc.perform(get("/waybills/drafts/" + draft.getId())
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(draft.getId()))
                    .andExpect(jsonPath("$.barcode").value("DRF-123"))
                    .andExpect(jsonPath("$.draftStatus").value("PENDING"))
                    .andExpect(jsonPath("$.recipientUserId").value(RECIPIENT_USER))
                    .andExpect(jsonPath("$.weightDeclared").value(1.0))
                    .andExpect(jsonPath("$.estimatedPrice").exists())
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("Должен вернуть 404 если черновик не найден")
        void shouldReturn404WhenDraftNotFound() throws Exception {
            // When & Then
            mockMvc.perform(get("/waybills/drafts/999999")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("DRAFT_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Draft not found with id: 999999"));
        }
    }

    // ===========================
    // GET /waybills/drafts/by-barcode/{barcode}
    // ===========================

    @Nested
    @DisplayName("GET /waybills/drafts/by-barcode/{barcode} - получение по штрих-коду")
    class GetDraftByBarcode {

        @Test
        @DisplayName("Должен вернуть черновик по barcode")
        void shouldReturnDraftByBarcode() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-BARCODE-999", DraftStatus.CONFIRMED);

            // When & Then
            mockMvc.perform(get("/waybills/drafts/by-barcode/DRF-BARCODE-999")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(draft.getId()))
                    .andExpect(jsonPath("$.barcode").value("DRF-BARCODE-999"))
                    .andExpect(jsonPath("$.draftStatus").value("CONFIRMED"));
        }

        @Test
        @DisplayName("Должен вернуть 404 если barcode не найден")
        void shouldReturn404WhenBarcodeNotFound() throws Exception {
            // When & Then
            mockMvc.perform(get("/waybills/drafts/by-barcode/NON-EXISTENT")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("DRAFT_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Draft not found with barcode: NON-EXISTENT"));
        }
    }

    // ===========================
    // PUT /waybills/drafts/{id}
    // ===========================

    @Nested
    @DisplayName("PUT /waybills/drafts/{id} - обновление черновика")
    class UpdateDraft {

        @Test
        @DisplayName("Должен обновить адрес получателя")
        void shouldUpdateRecipientAddress() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-UPD-001", DraftStatus.PENDING);

            var updateRequest = UpdateDraftRequest.builder()
                    .recipientAddress("г. Казань, ул. Баумана, д. 20")
                    .build();

            // When & Then
            mockMvc.perform(put("/waybills/drafts/" + draft.getId())
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recipientAddress").value("г. Казань, ул. Баумана, д. 20"))
                    .andExpect(jsonPath("$.weightDeclared").value(1.0)); // Не изменилось
        }

        @Test
        @DisplayName("Должен обновить вес и пересчитать цену")
        void shouldUpdateWeightAndRecalculatePrice() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-UPD-002", DraftStatus.PENDING);
            var originalPrice = draft.getEstimatedPrice();

            var updateRequest = UpdateDraftRequest.builder()
                    .weightDeclared(new BigDecimal("2.5"))
                    .pricingRuleId(PRICING_RULE_ID)
                    .build();

            // When & Then
            mockMvc.perform(put("/waybills/drafts/" + draft.getId())
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.weightDeclared").value(2.5))
                    .andExpect(jsonPath("$.estimatedPrice").isNumber());

            // Проверяем, что цена изменилась
            var updatedDraft = draftRepository.findById(draft.getId()).orElseThrow();
            assertThat(updatedDraft.getEstimatedPrice()).isNotEqualByComparingTo(originalPrice);
        }

        @Test
        @DisplayName("Должен обновить габариты")
        void shouldUpdateDimensions() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-UPD-003", DraftStatus.PENDING);

            var newDimensions = DimensionsDto.builder()
                    .length(new BigDecimal("100"))
                    .width(new BigDecimal("200"))
                    .height(new BigDecimal("300"))
                    .build();

            var updateRequest = UpdateDraftRequest.builder()
                    .dimensions(newDimensions)
                    .pricingRuleId(PRICING_RULE_ID)
                    .build();

            // When & Then
            mockMvc.perform(put("/waybills/drafts/" + draft.getId())
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dimensions.length").value(100))
                    .andExpect(jsonPath("$.dimensions.width").value(200))
                    .andExpect(jsonPath("$.dimensions.height").value(300));
        }

        @Test
        @DisplayName("Должен обновить несколько полей одновременно")
        void shouldUpdateMultipleFields() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-UPD-004", DraftStatus.PENDING);

            var updateRequest = UpdateDraftRequest.builder()
                    .recipientUserId(TEST_USER_3)
                    .recipientAddress("Новый адрес")
                    .weightDeclared(new BigDecimal("0.5"))
                    .pricingRuleId(PRICING_RULE_ID)
                    .build();

            // When & Then
            mockMvc.perform(put("/waybills/drafts/" + draft.getId())
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recipientUserId").value(TEST_USER_3))
                    .andExpect(jsonPath("$.recipientAddress").value("Новый адрес"))
                    .andExpect(jsonPath("$.weightDeclared").value(0.5));
        }

        @Test
        @DisplayName("Должен вернуть 404 при обновлении несуществующего черновика")
        void shouldReturn404WhenUpdatingNonExistentDraft() throws Exception {
            // Given
            var updateRequest = UpdateDraftRequest.builder()
                    .recipientAddress("Новый адрес")
                    .build();

            // When & Then
            mockMvc.perform(put("/waybills/drafts/999999")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("DRAFT_NOT_FOUND"));
        }
    }

    // ===========================
    // DELETE /waybills/drafts/{id}
    // ===========================

    @Nested
    @DisplayName("DELETE /waybills/drafts/{id} - удаление черновика")
    class DeleteDraft {

        @Test
        @DisplayName("Должен успешно удалить черновик")
        void shouldDeleteDraftSuccessfully() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-DEL-001", DraftStatus.PENDING);
            Long draftId = draft.getId();

            // When & Then
            mockMvc.perform(delete("/waybills/drafts/" + draftId)
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            // Проверяем, что черновик действительно удален из БД
            assertThat(draftRepository.findById(draftId)).isEmpty();
        }

        @Test
        @DisplayName("Должен удалить CONFIRMED черновик")
        void shouldDeleteConfirmedDraft() throws Exception {
            // Given
            var draft = createDraft(TEST_USER_1, "DRF-DEL-002", DraftStatus.CONFIRMED);

            // When & Then
            mockMvc.perform(delete("/waybills/drafts/" + draft.getId())
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertThat(draftRepository.findById(draft.getId())).isEmpty();
        }

        @Test
        @DisplayName("Должен вернуть 404 при удалении несуществующего черновика")
        void shouldReturn404WhenDeletingNonExistentDraft() throws Exception {
            // When & Then
            mockMvc.perform(delete("/waybills/drafts/999999")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("DRAFT_NOT_FOUND"));
        }
    }

    // ===========================
    // Helper Methods
    // ===========================

    private void createTestUsers() {
        List<Long> userIds = List.of(TEST_USER_1, TEST_USER_2, TEST_USER_3, RECIPIENT_USER);
        for (Long userId : userIds) {
            jdbcTemplate.update("""
                INSERT INTO user_management.users
                (user_id, email, role_name)
                VALUES (?, ?, ?)
                ON CONFLICT (user_id) DO NOTHING
                """,
                    userId,
                    "user" + userId + "@test.com",
                    "CLIENT"
            );
        }
    }

    private void createTestPricingRule() {
        jdbcTemplate.update("""
            INSERT INTO shared_data.pricing_rules
            (pricing_rule_id, rule_name, base_price, price_per_kg, weight_min, weight_max)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (pricing_rule_id) DO UPDATE SET
                rule_name = EXCLUDED.rule_name,
                base_price = EXCLUDED.base_price,
                price_per_kg = EXCLUDED.price_per_kg,
                weight_min = EXCLUDED.weight_min,
                weight_max = EXCLUDED.weight_max
            """,
                PRICING_RULE_ID,
                "Test Rule",
                new BigDecimal("100.00"),      // base_price
                new BigDecimal("200.00"),      // price_per_kg
                new BigDecimal("0.01"),        // weight_min
                new BigDecimal("100.0")        // weight_max
        );
    }

    private Draft createDraft(Long userId, String barcode, DraftStatus status) {
        var draft = Draft.builder()
                .barcode(barcode)
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(RECIPIENT_USER)
                .recipientAddress("Тестовый адрес доставки")
                .weightDeclared(new BigDecimal("1.0"))
                .pricingRuleId(PRICING_RULE_ID)
                .dimensions(null)
                .estimatedPrice(new BigDecimal("300.00"))  // base_price(100) + price_per_kg(200) * weight(1.0) = 300
                .draftStatus(status)
                .createdAt(LocalDateTime.now())
                .build();

        return draftRepository.save(draft);
    }

    private JwtAuthenticationToken createMockAuthentication(Long userId) {
        var jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .subject(userId.toString())
                .claim("role", "CLIENT")
                .claim("phone", "89991234567")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        return new JwtAuthenticationToken(jwt);
    }
}