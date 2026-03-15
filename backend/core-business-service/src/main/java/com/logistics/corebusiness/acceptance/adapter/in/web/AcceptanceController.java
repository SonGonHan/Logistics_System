package com.logistics.corebusiness.acceptance.adapter.in.web;

import com.logistics.corebusiness.acceptance.adapter.in.web.api.CompleteAcceptanceOperation;
import com.logistics.corebusiness.acceptance.adapter.in.web.api.StartAcceptanceOperation;
import com.logistics.corebusiness.acceptance.adapter.in.web.dto.AcceptanceResponse;
import com.logistics.corebusiness.acceptance.adapter.in.web.dto.CompleteAcceptanceRequest;
import com.logistics.corebusiness.acceptance.adapter.in.web.dto.CompleteAcceptanceResponse;
import com.logistics.corebusiness.acceptance.adapter.in.web.dto.StartAcceptanceRequest;
import com.logistics.corebusiness.acceptance.application.port.in.CompleteAcceptanceUseCase;
import com.logistics.corebusiness.acceptance.application.port.in.StartAcceptanceUseCase;
import com.logistics.corebusiness.acceptance.application.port.in.command.CompleteAcceptanceCommand;
import com.logistics.corebusiness.acceptance.application.port.in.command.StartAcceptanceCommand;
import com.logistics.shared.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для приёмки посылок на ПВЗ.
 *
 * <h2>Назначение</h2>
 * Предоставляет endpoints для начала и завершения процесса приёмки посылки оператором ПВЗ.
 *
 * <h2>Архитектура</h2>
 * Thin-controller: не содержит бизнес-логики, формирует команды и делегирует выполнение
 * в application layer через Use Case интерфейсы.
 *
 * <h2>Security</h2>
 * ID пользователя извлекается из JWT токена через {@link SecurityUtils}.
 *
 * <h2>Endpoints</h2>
 * - POST /acceptance/start - начать приёмку по barcode
 * - POST /acceptance/{draftId}/complete - завершить приёмку и создать накладную
 */
@Tag(name = "Приёмка на ПВЗ", description = "REST API endpoints для приёмки посылок на пункте выдачи")
@RestController
@RequestMapping("/acceptance")
@RequiredArgsConstructor
public class AcceptanceController {

    private final StartAcceptanceUseCase startAcceptanceUseCase;
    private final CompleteAcceptanceUseCase completeAcceptanceUseCase;

    /**
     * Начинает приёмку посылки по штрих-коду черновика.
     *
     * @param authentication аутентификация пользователя
     * @param request тело запроса
     * @return найденный черновик для проверки оператором
     */
    @PostMapping("/start")
    @StartAcceptanceOperation
    public ResponseEntity<AcceptanceResponse> startAcceptance(
            Authentication authentication,
            @Valid @RequestBody StartAcceptanceRequest request
    ) {
        var operatorId = SecurityUtils.extractUserId(authentication);

        var command = StartAcceptanceCommand.builder()
                .operatorId(operatorId)
                .barcode(request.barcode())
                .facilityId(request.facilityId())
                .build();

        var response = startAcceptanceUseCase.start(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Завершает приёмку посылки и создаёт накладную.
     *
     * @param authentication аутентификация пользователя
     * @param draftId идентификатор черновика из path
     * @param request тело запроса
     * @return данные созданной накладной
     */
    @PostMapping("/{draftId}/complete")
    @CompleteAcceptanceOperation
    public ResponseEntity<CompleteAcceptanceResponse> completeAcceptance(
            Authentication authentication,
            @PathVariable Long draftId,
            @Valid @RequestBody CompleteAcceptanceRequest request
    ) {
        var operatorId = SecurityUtils.extractUserId(authentication);

        var command = CompleteAcceptanceCommand.builder()
                .operatorId(operatorId)
                .draftId(draftId)
                .facilityId(request.facilityId())
                .build();

        var response = completeAcceptanceUseCase.complete(command);
        return ResponseEntity.ok(response);
    }
}
