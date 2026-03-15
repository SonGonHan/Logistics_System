package com.logistics.corebusiness.waybill.adapter.in.web;

import com.logistics.corebusiness.waybill.adapter.in.web.api.CreateWaybillOperation;
import com.logistics.corebusiness.waybill.adapter.in.web.api.GetWaybillOperation;
import com.logistics.corebusiness.waybill.adapter.in.web.api.ListWaybillsOperation;
import com.logistics.corebusiness.waybill.adapter.in.web.api.UpdateWaybillStatusOperation;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.CreateWaybillRequest;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.UpdateWaybillStatusRequest;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.WaybillResponse;
import com.logistics.corebusiness.waybill.application.port.in.CreateWaybillUseCase;
import com.logistics.corebusiness.waybill.application.port.in.GetWaybillUseCase;
import com.logistics.corebusiness.waybill.application.port.in.ListWaybillsUseCase;
import com.logistics.corebusiness.waybill.application.port.in.UpdateWaybillStatusUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.CreateWaybillCommand;
import com.logistics.corebusiness.waybill.application.port.in.command.GetWaybillCommand;
import com.logistics.corebusiness.waybill.application.port.in.command.ListWaybillsCommand;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateWaybillStatusCommand;
import com.logistics.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST контроллер для работы с подтвержденными накладными.
 *
 * <h2>Назначение</h2>
 * Предоставляет endpoints для создания, чтения и изменения статуса накладных.
 *
 * <h2>Архитектура</h2>
 * Thin-controller: извлекает userId из JWT, формирует команды и делегирует работу use case слою.
 *
 * <h2>Security</h2>
 * ID пользователя извлекается из JWT токена через {@link SecurityUtils}.
 */
@Tag(name = "Накладные", description = "REST API endpoints для работы с подтвержденными накладными")
@RestController
@RequestMapping("/waybills")
@RequiredArgsConstructor
public class WaybillController {

    private final CreateWaybillUseCase createWaybillUseCase;
    private final GetWaybillUseCase getWaybillUseCase;
    private final ListWaybillsUseCase listWaybillsUseCase;
    private final UpdateWaybillStatusUseCase updateWaybillStatusUseCase;

    /**
     * POST /waybills
     * Создание подтвержденной накладной.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CreateWaybillOperation
    public ResponseEntity<WaybillResponse> createWaybill(
            Authentication authentication,
            @Valid @RequestBody CreateWaybillRequest request
    ) {
        var userId = SecurityUtils.extractUserId(authentication);

        var command = CreateWaybillCommand.builder()
                .waybillCreatorId(userId)
                .draftId(request.draftId())
                .senderUserId(request.recipientUserId() != null ? userId : null)
                .recipientUserId(request.recipientUserId())
                .recipientAddress(request.recipientAddress())
                .pricingRuleId(request.pricingRuleId())
                .build();

        var response = createWaybillUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /waybills/{id}
     * Получение накладной по ID.
     */
    @GetMapping("/{id}")
    @GetWaybillOperation
    public ResponseEntity<WaybillResponse> getWaybillById(@PathVariable Long id) {
        var command = GetWaybillCommand.builder()
                .id(id)
                .build();
        var response = getWaybillUseCase.get(command);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /waybills
     * Получение списка накладных текущего пользователя.
     */
    @GetMapping
    @ListWaybillsOperation
    public ResponseEntity<List<WaybillResponse>> listWaybills(Authentication authentication) {
        var userId = SecurityUtils.extractUserId(authentication);
        var command = ListWaybillsCommand.builder()
                .senderUserId(userId)
                .build();
        var response = listWaybillsUseCase.list(command);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /waybills/{id}/status
     * Обновление статуса накладной.
     */
    @PutMapping("/{id}/status")
    @UpdateWaybillStatusOperation
    public ResponseEntity<WaybillResponse> updateWaybillStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateWaybillStatusRequest request
    ) {
        var userId = SecurityUtils.extractUserId(authentication);

        var command = UpdateWaybillStatusCommand.builder()
                .waybillId(id)
                .newStatus(request.newStatus())
                .operatorId(userId)
                .facilityId(request.facilityId())
                .notes(request.notes())
                .build();

        var response = updateWaybillStatusUseCase.updateStatus(command);
        return ResponseEntity.ok(response);
    }
}
