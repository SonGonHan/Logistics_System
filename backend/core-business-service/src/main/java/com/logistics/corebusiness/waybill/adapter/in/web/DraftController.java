package com.logistics.corebusiness.waybill.adapter.in.web;

import com.logistics.shared.security.SecurityUtils;
import com.logistics.corebusiness.waybill.adapter.in.DraftControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.api.*;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.CreateDraftRequest;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.UpdateDraftRequest;
import com.logistics.corebusiness.waybill.application.port.in.*;
import com.logistics.corebusiness.waybill.application.port.in.command.*;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с черновиками накладных.
 *
 * <h2>Назначение</h2>
 * Предоставляет CRUD операции для черновиков накладных (создание, чтение, обновление, удаление).
 *
 * <h2>Архитектура</h2>
 * Thin-controller: не содержит бизнес-логики, формирует команды и делегирует выполнение
 * в application layer через Use Case интерфейсы.
 *
 * <h2>Security</h2>
 * ID пользователя извлекается из JWT токена через {@link SecurityUtils}.
 * Все операции требуют аутентификации (кроме эндпоинтов, явно исключенных в Security конфигурации).
 *
 * <h2>Endpoints</h2>
 * - POST /waybills/drafts — Создать черновик
 * - GET /waybills/drafts — Получить список черновиков пользователя
 * - GET /waybills/drafts/{id} — Получить черновик по ID
 * - GET /waybills/drafts/by-barcode/{barcode} — Получить черновик по штрих-коду
 * - PUT /waybills/drafts/{id} — Обновить черновик
 * - DELETE /waybills/drafts/{id} — Удалить черновик
 */
@Tag(
        name = "Черновики накладных",
        description = "REST API endpoints для работы с черновиками накладных: создание, просмотр, редактирование и удаление"
)
@RestController
@RequestMapping("/waybills/drafts")
@RequiredArgsConstructor
public class DraftController {

    private final CreateDraftUseCase createDraftUseCase;
    private final GetDraftUseCase getDraftUseCase;
    private final UpdateDraftUseCase updateDraftUseCase;
    private final DeleteDraftUseCase deleteDraftUseCase;
    private final GetUserDraftListUseCase getUserDraftListUseCase;

    /**
     * POST /waybills/drafts
     * Создание нового черновика накладной.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CreateDraftOperation
    public ResponseEntity<Void> createDraft(
            Authentication authentication,
            @Valid @RequestBody CreateDraftRequest request
    ) {
        var userId = SecurityUtils.extractUserId(authentication);

        var command = CreateDraftCommand.builder()
                .draftCreatorId(userId)
                .senderUserId(userId)
                .recipientUserId(request.recipientUserId())
                .recipientAddress(request.recipientAddress())
                .weightDeclared(request.weightDeclared())
                .pricingRuleId(request.pricingRuleId())
                .dimensions(DraftControllerMapper.toDimensions(request.dimensions()))
                .build();

        createDraftUseCase.create(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /waybills/drafts
     * Получение списка черновиков текущего пользователя.
     */
    @GetMapping
    @GetUserDraftListOperation
    public ResponseEntity<List<DraftResponse>> getUserDrafts(
            Authentication authentication,
            @RequestParam(required = false) DraftStatus status
    ) {
        var userId = SecurityUtils.extractUserId(authentication);

        var command = GetUserDraftListCommand.builder()
                .userId(userId)
                .status(status)
                .build();

        var drafts = getUserDraftListUseCase.get(command);

        return ResponseEntity.ok(drafts);
    }

    /**
     * GET /waybills/drafts/{id}
     * Получение черновика по ID.
     */
    @GetMapping("/{id}")
    @GetDraftOperation
    public ResponseEntity<DraftResponse> getDraftById(
            @PathVariable Long id
    ) {

        var command = GetDraftCommand.builder()
                .id(id)
                .build();

        var draft = getDraftUseCase.get(command);

        return ResponseEntity.ok(draft);
    }

    /**
     * GET /waybills/drafts/by-barcode/{barcode}
     * Получение черновика по штрих-коду.
     */
    @GetMapping("/by-barcode/{barcode}")
    @GetDraftByBarcodeOperation
    public ResponseEntity<DraftResponse> getDraftByBarcode(
            @PathVariable String barcode
    ) {
        var command = GetDraftCommand.builder()
                .barcode(barcode)
                .build();

        var draft = getDraftUseCase.get(command);

        return ResponseEntity.ok(draft);
    }

    /**
     * PUT /waybills/drafts/{id}
     * Обновление черновика.
     */
    @PutMapping("/{id}")
    @UpdateDraftOperation
    public ResponseEntity<DraftResponse> updateDraft(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateDraftRequest request
    ) {
        var userId = SecurityUtils.extractUserId(authentication);

        var command = UpdateDraftCommand.builder()
                .draftId(id)
                .userId(userId)
                .recipientUserId(request.recipientUserId())
                .recipientAddress(request.recipientAddress())
                .weightDeclared(request.weightDeclared())
                .dimensions(DraftControllerMapper.toDimensions(request.dimensions()))
                .pricingRuleId(request.pricingRuleId())
                .build();

        var updated = updateDraftUseCase.update(command);

        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /waybills/drafts/{id}
     * Удаление черновика.
     */
    @DeleteMapping("/{id}")
    @DeleteDraftOperation
    public ResponseEntity<Void> deleteDraft(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = SecurityUtils.extractUserId(authentication);

        var command = DeleteDraftCommand.builder()
                .draftId(id)
                .userId(userId)
                .build();

        deleteDraftUseCase.delete(command);

        return ResponseEntity.noContent().build();
    }

}
