package com.logistics.corebusiness.pricing.adapter.in.web;

import com.logistics.corebusiness.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для PricingRuleController.
 *
 * <h2>Что тестируем</h2>
 * - GET /pricing-rules возвращает только активные тарифы
 * - Корректность JSON-структуры ответа
 * - Фильтрацию истёкших правил
 */
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PricingRuleController - интеграционные тесты REST API")
class PricingRuleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearPricingRules() {
        jdbcTemplate.update("DELETE FROM shared_data.pricing_rules");
    }

    // ===========================
    // GET /pricing-rules
    // ===========================

    @Nested
    @DisplayName("GET /pricing-rules - получение активных тарифов")
    class GetActiveRules {

        @Test
        @DisplayName("Должен вернуть пустой список, если тарифов нет")
        void shouldReturnEmptyListWhenNoRules() throws Exception {
            mockMvc.perform(get("/pricing-rules"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Должен вернуть активные тарифы с корректной структурой")
        void shouldReturnActiveRulesWithCorrectStructure() throws Exception {
            insertRule(10L, "Тариф Город", "CITY", new BigDecimal("250.00"),
                    new BigDecimal("30.0"), null, null);
            insertRule(11L, "Тариф Регион", "REGIONAL", new BigDecimal("500.00"),
                    new BigDecimal("50.0"), null, null);

            mockMvc.perform(get("/pricing-rules"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].id", containsInAnyOrder(10, 11)))
                    .andExpect(jsonPath("$[*].ruleName",
                            containsInAnyOrder("Тариф Город", "Тариф Регион")))
                    .andExpect(jsonPath("$[*].deliveryZone",
                            containsInAnyOrder("CITY", "REGIONAL")))
                    .andExpect(jsonPath("$[*].basePrice", everyItem(notNullValue())))
                    .andExpect(jsonPath("$[*].weightMin", everyItem(is(0))))
                    .andExpect(jsonPath("$[0].weightMax").exists());
        }

        @Test
        @DisplayName("Должен вернуть weightMin = 0 для всех тарифов")
        void shouldReturnZeroWeightMinForAllRules() throws Exception {
            insertRule(20L, "Любой тариф", "INTERCITY", new BigDecimal("300.00"),
                    new BigDecimal("20.0"), null, null);

            mockMvc.perform(get("/pricing-rules"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].weightMin").value(0));
        }

        @Test
        @DisplayName("Должен вернуть тариф с неограниченным весом (weightMax = null)")
        void shouldReturnRuleWithNullWeightMax() throws Exception {
            insertRule(30L, "Безлимитный", "CITY", new BigDecimal("999.00"),
                    null, null, null);

            mockMvc.perform(get("/pricing-rules"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].weightMax").doesNotExist());
        }

        @Test
        @DisplayName("Должен исключить истёкшие тарифы (effectiveTo в прошлом)")
        void shouldExcludeExpiredRules() throws Exception {
            insertRule(40L, "Активный тариф", "CITY", new BigDecimal("150.00"),
                    new BigDecimal("15.0"), null, null);
            insertRule(41L, "Истёкший тариф", "CITY", new BigDecimal("150.00"),
                    new BigDecimal("15.0"), null, LocalDate.now().minusDays(1));

            mockMvc.perform(get("/pricing-rules"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(40));
        }

        @Test
        @DisplayName("Должен исключить тарифы, которые ещё не начали действовать")
        void shouldExcludeFutureRules() throws Exception {
            insertRule(50L, "Будущий тариф", "CITY", new BigDecimal("100.00"),
                    new BigDecimal("10.0"), LocalDate.now().plusDays(1), null);
            insertRule(51L, "Текущий тариф", "CITY", new BigDecimal("200.00"),
                    new BigDecimal("20.0"), null, null);

            mockMvc.perform(get("/pricing-rules"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(51));
        }
    }

    // ===========================
    // Helper Methods
    // ===========================

    private void insertRule(Long id, String name, String zone, BigDecimal basePrice,
                            BigDecimal weightMax, LocalDate effectiveFrom, LocalDate effectiveTo) {
        jdbcTemplate.update("""
            INSERT INTO shared_data.pricing_rules
            (pricing_rule_id, rule_name, delivery_zone, base_price, weight_max,
             max_length_cm, max_width_cm, max_height_cm, effective_from, effective_to)
            VALUES (?, ?, ?, ?, ?, 60, 40, 30, ?, ?)
            """,
                id, name, zone, basePrice, weightMax, effectiveFrom, effectiveTo);
    }
}
