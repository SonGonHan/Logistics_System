package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.application.port.in.CreateDraftUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.CreateDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.application.port.out.RecipientUserPort;
import com.logistics.corebusiness.waybill.application.util.BarcodeGenerator;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import com.logistics.shared.pricing_rule.PricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Сервис создания черновика накладной.
 *
 * <h2>Бизнес-логика</h2>
 * - Генерирует уникальный barcode
 * - Рассчитывает estimatedPrice = basePrice выбранного тарифного плана
 * - Устанавливает начальный статус PENDING
 * - Сохраняет черновик в БД
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CreateDraftService implements CreateDraftUseCase {

    private final DraftRepository repository;
    private final BarcodeGenerator barcodeGenerator;
    private final PricingRuleService pricingRuleService;
    private final RecipientUserPort recipientUserPort;

    @Override
    public void create(CreateDraftCommand command) {
        var barcode = barcodeGenerator.generate();
        var estimatedPrice = pricingRuleService.calculatePrice(command.pricingRuleId());
        var recipientUserId = recipientUserPort.findOrCreateByPhone(command.recipientPhone());
        Draft draft = Draft.builder()
                .barcode(barcode)
                .draftCreatorId(command.draftCreatorId())
                .senderUserId(command.senderUserId())
                .recipientUserId(recipientUserId)
                .recipientAddress(command.recipientAddress())
                .pricingRuleId(command.pricingRuleId())
                .estimatedPrice(estimatedPrice)
                .draftStatus(DraftStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(draft);
    }
}