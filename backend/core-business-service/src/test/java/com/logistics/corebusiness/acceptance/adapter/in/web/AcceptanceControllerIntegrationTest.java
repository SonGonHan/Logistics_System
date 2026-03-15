package com.logistics.corebusiness.acceptance.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.corebusiness.IntegrationTest;
import com.logistics.corebusiness.acceptance.adapter.in.web.dto.CompleteAcceptanceRequest;
import com.logistics.corebusiness.acceptance.adapter.in.web.dto.StartAcceptanceRequest;
import jakarta.persistence.EntityManager;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для AcceptanceController.
 *
 * <h2>Что тестируем</h2>
 * - Полный путь HTTP -> Controller -> Service -> Repository -> DB
 * - Валидацию входных данных
 * - Обработку ошибок приёмки
 * - Побочные эффекты в БД: создание накладной, обновление черновика, историю статусов
 */
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AcceptanceController - интеграционные тесты REST API")
class AcceptanceControllerIntegrationTest {

    private static final Long OPERATOR_ID = 1001L;
    private static final Long SENDER_ID = 1002L;
    private static final Long RECIPIENT_ID = 1003L;
    private static final Long FACILITY_ID = 2001L;
    private static final Long PRICING_RULE_ID = 3001L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void cleanTables() {
        jdbcTemplate.update("DELETE FROM waybill_service.waybill_status_history");
        jdbcTemplate.update("DELETE FROM waybill_service.waybills");
        jdbcTemplate.update("DELETE FROM waybill_service.waybill_drafts");
        jdbcTemplate.update("DELETE FROM user_management.audit_logs WHERE user_id IN (?, ?, ?)", OPERATOR_ID, SENDER_ID, RECIPIENT_ID);
    }

    @Nested
    @DisplayName("POST /acceptance/start - начало приёмки")
    class StartAcceptance {

        @Test
        @DisplayName("Должен начать приёмку для PENDING черновика")
        void shouldStartAcceptanceForPendingDraft() throws Exception {
            insertUser(OPERATOR_ID, "+79990000001", "PVZ_OPERATOR");
            insertUser(SENDER_ID, "+79990000002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990000003", "CLIENT");
            insertFacility(FACILITY_ID, "PVZ", "Тестовый ПВЗ");
            insertPricingRule(PRICING_RULE_ID, "Test Rule", "CITY", new BigDecimal("450.00"));
            Long draftId = insertDraft("DRF-START-001", "PENDING", SENDER_ID, RECIPIENT_ID, PRICING_RULE_ID);

            var request = StartAcceptanceRequest.builder()
                    .barcode("DRF-START-001")
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/start")
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.draftId").value(draftId))
                    .andExpect(jsonPath("$.barcode").value("DRF-START-001"))
                    .andExpect(jsonPath("$.senderUserId").value(SENDER_ID))
                    .andExpect(jsonPath("$.recipientUserId").value(RECIPIENT_ID))
                    .andExpect(jsonPath("$.recipientAddress").value("г. Москва, ул. Тестовая, д. 1"))
                    .andExpect(jsonPath("$.estimatedPrice").value(450.00))
                    .andExpect(jsonPath("$.draftStatus").value("PENDING"));
        }

        @Test
        @DisplayName("Должен вернуть 404 когда barcode не найден")
        void shouldReturn404WhenBarcodeNotFound() throws Exception {
            var request = StartAcceptanceRequest.builder()
                    .barcode("UNKNOWN-BARCODE")
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/start")
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("ACCEPTANCE_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Acceptance draft not found with barcode: UNKNOWN-BARCODE"));
        }

        @Test
        @DisplayName("Должен вернуть 409 когда черновик уже CONFIRMED")
        void shouldReturn409WhenDraftAlreadyConfirmed() throws Exception {
            insertUser(SENDER_ID, "+79990000002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990000003", "CLIENT");
            insertPricingRule(PRICING_RULE_ID, "Test Rule", "CITY", new BigDecimal("450.00"));
            insertDraft("DRF-START-002", "CONFIRMED", SENDER_ID, RECIPIENT_ID, PRICING_RULE_ID);

            var request = StartAcceptanceRequest.builder()
                    .barcode("DRF-START-002")
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/start")
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("INVALID_ACCEPTANCE_STATUS"));
        }

        @Test
        @DisplayName("Должен вернуть 409 когда черновик CANCELLED")
        void shouldReturn409WhenDraftCancelled() throws Exception {
            insertUser(SENDER_ID, "+79990000002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990000003", "CLIENT");
            insertPricingRule(PRICING_RULE_ID, "Test Rule", "CITY", new BigDecimal("450.00"));
            insertDraft("DRF-START-003", "CANCELLED", SENDER_ID, RECIPIENT_ID, PRICING_RULE_ID);

            var request = StartAcceptanceRequest.builder()
                    .barcode("DRF-START-003")
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/start")
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("INVALID_ACCEPTANCE_STATUS"));
        }

        @Test
        @DisplayName("Должен вернуть 400 когда barcode пустой")
        void shouldReturn400WhenBarcodeBlank() throws Exception {
            var request = StartAcceptanceRequest.builder()
                    .barcode("")
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/start")
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.fields.barcode").exists());
        }
    }

    @Nested
    @DisplayName("POST /acceptance/{draftId}/complete - завершение приёмки")
    class CompleteAcceptance {

        @Test
        @DisplayName("Должен завершить приёмку и создать накладную")
        void shouldCompleteAcceptanceAndCreateWaybill() throws Exception {
            insertUser(OPERATOR_ID, "+79990000001", "PVZ_OPERATOR");
            insertUser(SENDER_ID, "+79990000002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990000003", "CLIENT");
            insertFacility(FACILITY_ID, "PVZ", "Тестовый ПВЗ");
            insertPricingRule(PRICING_RULE_ID, "Test Rule", "CITY", new BigDecimal("550.00"));
            Long draftId = insertDraft("DRF-COMPLETE-001", "PENDING", SENDER_ID, RECIPIENT_ID, PRICING_RULE_ID);

            var request = CompleteAcceptanceRequest.builder()
                    .draftId(draftId)
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/{draftId}/complete", draftId)
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.waybillId").isNumber())
                    .andExpect(jsonPath("$.waybillNumber").value(matchesPattern("^WB-\\d{6}-\\d{6}$")))
                    .andExpect(jsonPath("$.finalPrice").value(550.00))
                    .andExpect(jsonPath("$.waybillStatus").value("ACCEPTED_AT_PVZ"))
                    .andExpect(jsonPath("$.acceptedAt").exists());

            entityManager.flush();
            entityManager.clear();

            Integer waybillCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM waybill_service.waybills",
                    Integer.class
            );
            assertThat(waybillCount).isEqualTo(1);

            String draftStatus = jdbcTemplate.queryForObject(
                    "SELECT draft_status FROM waybill_service.waybill_drafts WHERE draft_id = ?",
                    String.class,
                    draftId
            );
            assertThat(draftStatus).isEqualTo("CONFIRMED");

            Integer historyCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM waybill_service.waybill_status_history WHERE status = 'ACCEPTED_AT_PVZ' AND facility_id = ?",
                    Integer.class,
                    FACILITY_ID
            );
            assertThat(historyCount).isEqualTo(1);
        }

        @Test
        @DisplayName("Должен вернуть 404 когда draftId не найден")
        void shouldReturn404WhenDraftIdNotFound() throws Exception {
            insertUser(OPERATOR_ID, "+79990000001", "PVZ_OPERATOR");
            insertFacility(FACILITY_ID, "PVZ", "Тестовый ПВЗ");

            var request = CompleteAcceptanceRequest.builder()
                    .draftId(999999L)
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/{draftId}/complete", 999999L)
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("ACCEPTANCE_NOT_FOUND"));
        }

        @Test
        @DisplayName("Должен вернуть 409 когда черновик не в статусе PENDING")
        void shouldReturn409WhenDraftNotPending() throws Exception {
            insertUser(OPERATOR_ID, "+79990000001", "PVZ_OPERATOR");
            insertUser(SENDER_ID, "+79990000002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990000003", "CLIENT");
            insertPricingRule(PRICING_RULE_ID, "Test Rule", "CITY", new BigDecimal("550.00"));
            Long draftId = insertDraft("DRF-COMPLETE-002", "CONFIRMED", SENDER_ID, RECIPIENT_ID, PRICING_RULE_ID);

            var request = CompleteAcceptanceRequest.builder()
                    .draftId(draftId)
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/{draftId}/complete", draftId)
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("INVALID_ACCEPTANCE_STATUS"));
        }

        @Test
        @DisplayName("Должен сгенерировать уникальный номер накладной")
        void shouldGenerateUniqueWaybillNumber() throws Exception {
            insertUser(OPERATOR_ID, "+79990000001", "PVZ_OPERATOR");
            insertUser(SENDER_ID, "+79990000002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990000003", "CLIENT");
            insertFacility(FACILITY_ID, "PVZ", "Тестовый ПВЗ");
            insertPricingRule(PRICING_RULE_ID, "Test Rule", "CITY", new BigDecimal("550.00"));
            Long draftId = insertDraft("DRF-COMPLETE-003", "PENDING", SENDER_ID, RECIPIENT_ID, PRICING_RULE_ID);

            var request = CompleteAcceptanceRequest.builder()
                    .draftId(draftId)
                    .facilityId(FACILITY_ID)
                    .build();

            mockMvc.perform(post("/acceptance/{draftId}/complete", draftId)
                            .principal(createMockAuthentication(OPERATOR_ID, "PVZ_OPERATOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.waybillNumber").value(matchesPattern("^WB-\\d{6}-\\d{6}$")));
        }
    }

    private void insertUser(Long id, String phone, String role) {
        jdbcTemplate.update("""
                INSERT INTO user_management.users
                (user_id, phone, role_name, user_status, created_at)
                VALUES (?, ?, ?, 'ACTIVE', ?)
                ON CONFLICT (user_id) DO UPDATE SET
                    phone = EXCLUDED.phone,
                    role_name = EXCLUDED.role_name,
                    user_status = EXCLUDED.user_status
                """,
                id,
                phone,
                role,
                LocalDateTime.now()
        );
    }

    private Long insertDraft(String barcode, String status, Long senderId, Long recipientId, Long pricingRuleId) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO waybill_service.waybill_drafts
                (barcode, draft_creator_id, sender_user_id, recipient_user_id, recipient_address, pricing_rule_id, estimated_price, draft_status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING draft_id
                """,
                Long.class,
                barcode,
                senderId,
                senderId,
                recipientId,
                "г. Москва, ул. Тестовая, д. 1",
                pricingRuleId,
                new BigDecimal("450.00"),
                status,
                LocalDateTime.now()
        );
    }

    private void insertPricingRule(Long id, String name, String zone, BigDecimal basePrice) {
        jdbcTemplate.update("""
                INSERT INTO shared_data.pricing_rules
                (pricing_rule_id, rule_name, delivery_zone, base_price, weight_max, max_length_cm, max_width_cm, max_height_cm, effective_from, effective_to)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (pricing_rule_id) DO UPDATE SET
                    rule_name = EXCLUDED.rule_name,
                    delivery_zone = EXCLUDED.delivery_zone,
                    base_price = EXCLUDED.base_price,
                    weight_max = EXCLUDED.weight_max,
                    max_length_cm = EXCLUDED.max_length_cm,
                    max_width_cm = EXCLUDED.max_width_cm,
                    max_height_cm = EXCLUDED.max_height_cm,
                    effective_from = EXCLUDED.effective_from,
                    effective_to = EXCLUDED.effective_to
                """,
                id,
                name,
                zone,
                basePrice,
                new BigDecimal("10.00"),
                new BigDecimal("60.00"),
                new BigDecimal("40.00"),
                new BigDecimal("30.00"),
                LocalDate.now().minusDays(1),
                null
        );
    }

    private void insertFacility(Long id, String type, String name) {
        jdbcTemplate.update("""
                INSERT INTO shared_data.company_facilities
                (facility_id, facility_type, facility_name, address, latitude, longitude, created_at, closed_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (facility_id) DO UPDATE SET
                    facility_type = EXCLUDED.facility_type,
                    facility_name = EXCLUDED.facility_name,
                    address = EXCLUDED.address,
                    latitude = EXCLUDED.latitude,
                    longitude = EXCLUDED.longitude,
                    closed_date = EXCLUDED.closed_date
                """,
                id,
                type,
                name,
                "г. Москва, ул. Пункт выдачи, д. 5",
                new BigDecimal("55.7558000"),
                new BigDecimal("37.6176000"),
                LocalDateTime.now(),
                null
        );
    }

    private JwtAuthenticationToken createMockAuthentication(Long userId, String role) {
        var jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .subject(userId.toString())
                .claim("role", role)
                .claim("phone", "+79990000000")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        return new JwtAuthenticationToken(jwt);
    }
}