package com.logistics.corebusiness.waybill.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.corebusiness.IntegrationTest;
import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.shared.redis.service.RedisService;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.CreateWaybillRequest;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.UpdateWaybillStatusRequest;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.application.port.out.WaybillRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.corebusiness.waybill.domain.Waybill;
import com.logistics.corebusiness.waybill.domain.WaybillStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WaybillController - интеграционные тесты REST API")
class WaybillControllerIntegrationTest {

    private static final Long TEST_USER_1 = 1L;
    private static final Long TEST_USER_2 = 2L;
    private static final Long RECIPIENT_USER = 11L;
    private static final Long PRICING_RULE_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DraftRepository draftRepository;

    @Autowired
    private WaybillRepository waybillRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private CreateAuditLogUseCase auditLogUseCase;

    @MockBean
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        createTestUsers();
        createTestPricingRule();
    }

    @Nested
    @DisplayName("POST /waybills - создание накладной")
    class CreateWaybill {

        @Test
        @DisplayName("Должен создать накладную из черновика")
        void shouldCreateWaybillFromDraft() throws Exception {
            var draft = createDraft(TEST_USER_1, RECIPIENT_USER, DraftStatus.PENDING);

            var request = CreateWaybillRequest.builder()
                    .draftId(draft.getId())
                    .build();

            mockMvc.perform(post("/waybills")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.senderUserId").value(TEST_USER_1))
                    .andExpect(jsonPath("$.recipientUserId").value(RECIPIENT_USER))
                    .andExpect(jsonPath("$.status").value("ACCEPTED_AT_PVZ"));
        }

        @Test
        @DisplayName("Должен создать накладную без черновика")
        void shouldCreateWaybillWithoutDraft() throws Exception {
            var request = CreateWaybillRequest.builder()
                    .recipientUserId(RECIPIENT_USER)
                    .recipientAddress("Казань, ул. Баумана, д. 5")
                    .pricingRuleId(PRICING_RULE_ID)
                    .build();

            mockMvc.perform(post("/waybills")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.recipientUserId").value(RECIPIENT_USER))
                    .andExpect(jsonPath("$.status").value("ACCEPTED_AT_PVZ"));
        }

        @Test
        @DisplayName("Должен вернуть 404 когда черновик не найден")
        void shouldReturn404WhenDraftNotFound() throws Exception {
            var request = CreateWaybillRequest.builder()
                    .draftId(999999L)
                    .build();

            mockMvc.perform(post("/waybills")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("DRAFT_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("GET /waybills/{id} - получение накладной")
    class GetWaybill {

        @Test
        @DisplayName("Должен получить накладную по ID")
        void shouldGetWaybillById() throws Exception {
            var waybill = createWaybill(TEST_USER_1, RECIPIENT_USER, WaybillStatus.ACCEPTED_AT_PVZ);

            mockMvc.perform(get("/waybills/" + waybill.getId())
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(waybill.getId()))
                    .andExpect(jsonPath("$.waybillNumber").value(waybill.getWaybillNumber()));
        }

        @Test
        @DisplayName("Должен вернуть 404 когда накладная не найдена")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(get("/waybills/999999")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("WAYBILL_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("GET /waybills - список накладных")
    class ListWaybills {

        @Test
        @DisplayName("Должен вернуть список накладных пользователя")
        void shouldListUserWaybills() throws Exception {
            createWaybill(TEST_USER_1, RECIPIENT_USER, WaybillStatus.ACCEPTED_AT_PVZ);
            createWaybill(TEST_USER_1, TEST_USER_2, WaybillStatus.IN_TRANSIT);
            createWaybill(TEST_USER_2, RECIPIENT_USER, WaybillStatus.DELIVERED);

            mockMvc.perform(get("/waybills")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].senderUserId", containsInAnyOrder(1, 1)));
        }

        @Test
        @DisplayName("Должен вернуть пустой список")
        void shouldReturnEmptyList() throws Exception {
            createWaybill(TEST_USER_2, RECIPIENT_USER, WaybillStatus.ACCEPTED_AT_PVZ);

            mockMvc.perform(get("/waybills")
                            .principal(createMockAuthentication(TEST_USER_1)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("PUT /waybills/{id}/status - изменение статуса")
    class UpdateStatus {

        @Test
        @DisplayName("Должен успешно обновить статус")
        void shouldUpdateStatusSuccessfully() throws Exception {
            var waybill = createWaybill(TEST_USER_1, RECIPIENT_USER, WaybillStatus.ACCEPTED_AT_PVZ);
            var request = UpdateWaybillStatusRequest.builder()
                    .newStatus(WaybillStatus.IN_TRANSIT)
                    .facilityId(7L)
                    .notes("Отправлено")
                    .build();

            mockMvc.perform(put("/waybills/" + waybill.getId() + "/status")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_TRANSIT"));

            var updated = waybillRepository.findById(waybill.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(WaybillStatus.IN_TRANSIT);
        }

        @Test
        @DisplayName("Должен вернуть 409 при недопустимом переходе статуса")
        void shouldReturn409OnInvalidTransition() throws Exception {
            var waybill = createWaybill(TEST_USER_1, RECIPIENT_USER, WaybillStatus.ACCEPTED_AT_PVZ);
            var request = UpdateWaybillStatusRequest.builder()
                    .newStatus(WaybillStatus.DELIVERED)
                    .build();

            mockMvc.perform(put("/waybills/" + waybill.getId() + "/status")
                            .principal(createMockAuthentication(TEST_USER_1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("INVALID_STATUS_TRANSITION"));
        }
    }

    private void createTestUsers() {
        List<Long> userIds = List.of(TEST_USER_1, TEST_USER_2, RECIPIENT_USER);
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
            (pricing_rule_id, rule_name, delivery_zone, base_price, weight_max, max_length_cm, max_width_cm, max_height_cm)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (pricing_rule_id) DO UPDATE SET
                rule_name = EXCLUDED.rule_name,
                delivery_zone = EXCLUDED.delivery_zone,
                base_price = EXCLUDED.base_price,
                weight_max = EXCLUDED.weight_max,
                max_length_cm = EXCLUDED.max_length_cm,
                max_width_cm = EXCLUDED.max_width_cm,
                max_height_cm = EXCLUDED.max_height_cm
            """,
                PRICING_RULE_ID,
                "Test Rule",
                "CITY",
                new BigDecimal("100.00"),
                new BigDecimal("100.0"),
                new BigDecimal("60.00"),
                new BigDecimal("40.00"),
                new BigDecimal("30.00")
        );
    }

    private Draft createDraft(Long senderId, Long recipientUserId, DraftStatus status) {
        return draftRepository.save(Draft.builder()
                .barcode("DRF-" + System.nanoTime())
                .draftCreatorId(senderId)
                .senderUserId(senderId)
                .recipientUserId(recipientUserId)
                .recipientAddress("Тестовый адрес")
                .pricingRuleId(PRICING_RULE_ID)
                .estimatedPrice(new BigDecimal("100.00"))
                .draftStatus(status)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private Waybill createWaybill(Long senderId, Long recipientUserId, WaybillStatus status) {
        return waybillRepository.save(Waybill.builder()
                .waybillNumber("WB-" + System.nanoTime())
                .waybillCreatorId(senderId)
                .senderUserId(senderId)
                .recipientUserId(recipientUserId)
                .recipientAddress("Тестовый адрес")
                .pricingRuleId(PRICING_RULE_ID)
                .finalPrice(new BigDecimal("200.00"))
                .status(status)
                .createdAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build());
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
