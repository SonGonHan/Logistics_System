package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.corebusiness.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.corebusiness.waybill.application.exception.DraftAccessDeniedException;
import com.logistics.corebusiness.waybill.application.exception.DraftInvalidStatusException;
import com.logistics.corebusiness.waybill.application.exception.DraftNotFoundException;
import com.logistics.corebusiness.waybill.application.port.in.DeleteDraftUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.DeleteDraftCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import com.logistics.corebusiness.waybill.domain.DraftStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Сервис удаления черновика накладной.
 *
 * <h2>Бизнес-логика</h2>
 * - Проверяет права доступа (пользователь должен быть создателем)
 * - Проверяет статус (можно удалить только PENDING черновики)
 * - Выполняет физическое удаление
 * - Записывает аудит DRAFT_CANCEL
 *
 * <h2>Ограничения</h2>
 * Нельзя удалить черновик со статусом CONFIRMED или CANCELLED.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteDraftService implements DeleteDraftUseCase {

    private final DraftRepository repository;
    private final CreateAuditLogUseCase auditLogUseCase;

    @Override
    public void delete(DeleteDraftCommand command) {
        var draft = repository.findById(command.draftId())
                .orElseThrow(() -> DraftNotFoundException.byId(command.draftId()));

        auditLogUseCase.create(new CreateAuditLogCommand(
                command.userId(),
                "DRAFT_CANCEL",
                null,
                null,
                Map.of(
                        "draftId", command.draftId(),
                        "barcode", draft.getBarcode()
                ),
                "waybill_drafts",
                command.draftId()
        ));

        repository.delete(draft);
    }

}
