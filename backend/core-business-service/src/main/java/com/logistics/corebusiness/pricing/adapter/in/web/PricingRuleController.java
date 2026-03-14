package com.logistics.corebusiness.pricing.adapter.in.web;

import com.logistics.corebusiness.pricing.adapter.in.web.dto.PricingRuleResponse;
import com.logistics.shared.pricing_rule.PricingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST контроллер для получения тарифных правил ценообразования.
 *
 * <h2>Endpoints</h2>
 * - GET /pricing-rules — Получить список всех активных тарифов
 */
@Tag(name = "Тарифы", description = "Получение активных тарифных правил доставки")
@RestController
@RequestMapping("/pricing-rules")
@RequiredArgsConstructor
public class PricingRuleController {

    private final PricingRuleService pricingRuleService;

    /**
     * GET /pricing-rules
     * Возвращает список всех активных тарифных правил.
     */
    @GetMapping
    @Operation(summary = "Получить активные тарифы", description = "Возвращает список тарифных правил, действующих на текущую дату")
    public ResponseEntity<List<PricingRuleResponse>> getActiveRules() {
        var rules = pricingRuleService.getActiveRules().stream()
                .map(rule -> PricingRuleResponse.builder()
                        .id(rule.getId())
                        .ruleName(rule.getRuleName())
                        .deliveryZone(rule.getDeliveryZone())
                        .weightMin(BigDecimal.ZERO)
                        .weightMax(rule.getWeightMax())
                        .basePrice(rule.getBasePrice())
                        .build())
                .toList();

        return ResponseEntity.ok(rules);
    }
}