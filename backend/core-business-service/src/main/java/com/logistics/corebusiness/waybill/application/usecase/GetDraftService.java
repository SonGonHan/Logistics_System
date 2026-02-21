package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.DraftControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.exception.DraftAccessDeniedException;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.exception.DraftValidationException;
import com.logistics.corebusiness.waybill.application.port.in.GetDraftUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.GetDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис получения черновика накладной.
 *
 * <h2>Бизнес-логика</h2>
 * - Поддерживает поиск по ID или barcode
 * - Проверяет права доступа (пользователь должен быть создателем черновика)
 * - Возвращает базовую информацию (DraftResponse)
 *
 * <h2>Безопасность</h2>
 * Пользователь может получить только свои черновики.
 * Проверка: draft.getDraftCreatorId() == command.getUserId()
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDraftService implements GetDraftUseCase {

    private final DraftRepository repository;

    @Override
    public DraftResponse get(GetDraftCommand command) {
        var draft = findDraft(command);
        return DraftControllerMapper.toResponse(draft);
    }

    private Draft findDraft(GetDraftCommand command) {
        if (command.id() != null) {
            return repository.findById(command.id())
                    .orElseThrow(() -> DraftNotFoundException.byId(command.id()));
        } else if (command.barcode() != null) {
            return repository.findByBarcode(command.barcode())
                    .orElseThrow(() -> DraftNotFoundException.byBarcode(command.barcode()));
        } else {
            throw new DraftValidationException("Either id or barcode must be provided");
        }
    }

}
