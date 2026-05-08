package com.logistics.corebusiness.rating.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.corebusiness.IntegrationTest;
import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.rating.adapter.in.web.dto.SubmitRatingRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RatingController - интеграционные тесты REST API")
class RatingControllerIntegrationTest {

    private static final Long USER_ID = 1001L;
    private static final Long SENDER_ID = 1002L;
    private static final Long RECIPIENT_ID = 1003L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private CreateAuditLogUseCase auditLogUseCase;

    @BeforeEach
    void cleanTables() {
        jdbcTemplate.update("DELETE FROM rating_service.ratings_reviews");
        jdbcTemplate.update("DELETE FROM waybill_service.waybill_status_history");
        jdbcTemplate.update("DELETE FROM waybill_service.waybills");
        jdbcTemplate.update("DELETE FROM user_management.users WHERE user_id IN (?, ?, ?)", USER_ID, SENDER_ID, RECIPIENT_ID);
    }

    @Nested
    @DisplayName("POST /ratings/submit - отправка оценки")
    class SubmitRating {

        @Test
        @DisplayName("Должен успешно создать рейтинг")
        void shouldSubmitRatingSuccessfully() throws Exception {
            insertUser(USER_ID, "+79990001001", "CLIENT");
            insertUser(SENDER_ID, "+79990001002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990001003", "CLIENT");
            var waybillId = insertWaybill("WB-RATING-001", "DELIVERED", USER_ID, SENDER_ID, RECIPIENT_ID);
            var request = SubmitRatingRequest.builder()
                    .waybillId(waybillId)
                    .score(5)
                    .comment("Отличная доставка")
                    .build();

            mockMvc.perform(post("/ratings/submit")
                            .principal(createMockAuthentication(USER_ID, "CLIENT"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.waybillId").value(waybillId))
                    .andExpect(jsonPath("$.userId").value(USER_ID))
                    .andExpect(jsonPath("$.score").value(5))
                    .andExpect(jsonPath("$.comment").value("Отличная доставка"))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("Должен вернуть 404 когда накладная не найдена")
        void shouldReturn404WhenWaybillNotFound() throws Exception {
            var request = SubmitRatingRequest.builder()
                    .waybillId(999999L)
                    .score(5)
                    .comment("Отличная доставка")
                    .build();

            mockMvc.perform(post("/ratings/submit")
                            .principal(createMockAuthentication(USER_ID, "CLIENT"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("WAYBILL_NOT_FOUND"));
        }

        @Test
        @DisplayName("Должен вернуть 400 когда накладная не доставлена")
        void shouldReturn400WhenWaybillNotDelivered() throws Exception {
            insertUser(USER_ID, "+79990001001", "CLIENT");
            insertUser(SENDER_ID, "+79990001002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990001003", "CLIENT");
            var waybillId = insertWaybill("WB-RATING-002", "IN_TRANSIT", USER_ID, SENDER_ID, RECIPIENT_ID);
            var request = SubmitRatingRequest.builder()
                    .waybillId(waybillId)
                    .score(5)
                    .comment("Отличная доставка")
                    .build();

            mockMvc.perform(post("/ratings/submit")
                            .principal(createMockAuthentication(USER_ID, "CLIENT"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("RATING_VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Должен вернуть 409 когда рейтинг уже существует")
        void shouldReturn409WhenRatingAlreadyExists() throws Exception {
            insertUser(USER_ID, "+79990001001", "CLIENT");
            insertUser(SENDER_ID, "+79990001002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990001003", "CLIENT");
            var waybillId = insertWaybill("WB-RATING-003", "DELIVERED", USER_ID, SENDER_ID, RECIPIENT_ID);
            insertRating(waybillId, USER_ID, 4);
            var request = SubmitRatingRequest.builder()
                    .waybillId(waybillId)
                    .score(5)
                    .comment("Отличная доставка")
                    .build();

            mockMvc.perform(post("/ratings/submit")
                            .principal(createMockAuthentication(USER_ID, "CLIENT"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("RATING_DUPLICATE"));
        }

        @Test
        @DisplayName("Должен вернуть 400 когда оценка вне диапазона")
        void shouldReturn400WhenScoreOutOfRange() throws Exception {
            var request = SubmitRatingRequest.builder()
                    .waybillId(1L)
                    .score(0)
                    .comment("Недопустимо")
                    .build();

            mockMvc.perform(post("/ratings/submit")
                            .principal(createMockAuthentication(USER_ID, "CLIENT"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.fields.score").exists());
        }
    }

    @Nested
    @DisplayName("GET /ratings/waybill/{waybillId} - получение оценки")
    class GetRating {

        @Test
        @DisplayName("Должен успешно получить рейтинг по ID накладной")
        void shouldGetRatingByWaybillId() throws Exception {
            insertUser(USER_ID, "+79990001001", "CLIENT");
            insertUser(SENDER_ID, "+79990001002", "CLIENT");
            insertUser(RECIPIENT_ID, "+79990001003", "CLIENT");
            var waybillId = insertWaybill("WB-RATING-004", "DELIVERED", USER_ID, SENDER_ID, RECIPIENT_ID);
            insertRating(waybillId, USER_ID, 4);

            mockMvc.perform(get("/ratings/waybill/{waybillId}", waybillId)
                            .principal(createMockAuthentication(USER_ID, "CLIENT")))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.waybillId").value(waybillId))
                    .andExpect(jsonPath("$.userId").value(USER_ID))
                    .andExpect(jsonPath("$.score").value(4))
                    .andExpect(jsonPath("$.comment").value("test"));
        }

        @Test
        @DisplayName("Должен вернуть 404 когда рейтинг не найден")
        void shouldReturn404WhenRatingNotFound() throws Exception {
            mockMvc.perform(get("/ratings/waybill/{waybillId}", 999999L)
                            .principal(createMockAuthentication(USER_ID, "CLIENT")))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("RATING_NOT_FOUND"));
        }
    }

    private void insertUser(Long userId, String phone, String role) {
        jdbcTemplate.update("""
                INSERT INTO user_management.users
                (user_id, phone, role_name, user_status, created_at)
                VALUES (?, ?, ?, 'ACTIVE', ?)
                ON CONFLICT (user_id) DO UPDATE SET
                    phone = EXCLUDED.phone,
                    role_name = EXCLUDED.role_name,
                    user_status = EXCLUDED.user_status
                """,
                userId,
                phone,
                role,
                LocalDateTime.now()
        );
    }

    private Long insertWaybill(String waybillNumber, String status, Long creatorId, Long senderId, Long recipientId) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO waybill_service.waybills
                (waybill_number, waybill_creator_id, sender_user_id, recipient_user_id, recipient_address, final_price, waybill_status, created_at)
                VALUES (?, ?, ?, ?, 'Test Address', ?, ?, ?)
                RETURNING waybill_id
                """,
                Long.class,
                waybillNumber,
                creatorId,
                senderId,
                recipientId,
                new BigDecimal("500.00"),
                status,
                LocalDateTime.now()
        );
    }

    private void insertRating(Long waybillId, Long userId, int score) {
        jdbcTemplate.update("""
                INSERT INTO rating_service.ratings_reviews (waybill_id, user_id, rating, review_text)
                VALUES (?, ?, ?, 'test')
                """,
                waybillId,
                userId,
                score
        );
    }

    private JwtAuthenticationToken createMockAuthentication(Long userId, String role) {
        var jwt = Jwt.withTokenValue("test-token")
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
