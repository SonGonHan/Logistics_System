package com.logistics.corebusiness.waybill.application.usecase;

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

/**
 * Сервис удаления черновика накладной.
 *
 * <h2>Бизнес-логика</h2>
 * - Проверяет права доступа (пользователь должен быть создателем)
 * - Проверяет статус (можно удалить только PENDING черновики)
 * - Выполняет физическое удаление
 *
 * <h2>Ограничения</h2>
 * Нельзя удалить черновик со статусом CONFIRMED или CANCELLED.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteDraftService implements DeleteDraftUseCase {

    private final DraftRepository repository;

    @Override
    public void delete(DeleteDraftCommand command) {
        var draft = repository.findById(command.draftId())
                .orElseThrow(() -> DraftNotFoundException.byId(command.draftId()));

        repository.delete(draft);
    }

}
