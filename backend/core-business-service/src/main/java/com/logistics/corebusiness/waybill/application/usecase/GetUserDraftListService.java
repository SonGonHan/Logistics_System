package com.logistics.corebusiness.waybill.application.usecase;

import com.logistics.corebusiness.waybill.adapter.in.DraftControllerMapper;
import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.port.in.GetUserDraftListUseCase;
import com.logistics.corebusiness.waybill.application.port.in.command.GetUserDraftListCommand;
import com.logistics.corebusiness.waybill.application.port.out.DraftRepository;
import com.logistics.corebusiness.waybill.domain.Draft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис получения списка черновиков пользователя.
 *
 * <h2>Бизнес-логика</h2>
 * - Возвращает все черновики, созданные пользователем
 * - Поддерживает фильтрацию по статусу (опционально)
 * - Сортировка по дате создания (newest first)
 *
 * <h2>Безопасность</h2>
 * Пользователь видит только свои черновики (по draftCreatorId).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserDraftListService implements GetUserDraftListUseCase {

    private final DraftRepository repository;

    @Override
    public List<DraftResponse> get(GetUserDraftListCommand command) {
        List<Draft> drafts = findDrafts(command);

        return drafts.stream()
                .map(DraftControllerMapper::toResponse)
                .collect(Collectors.toList());
    }

    private List<Draft> findDrafts(GetUserDraftListCommand command) {
        var stream = repository.findByDraftCreatorId(command.userId()).stream();

        // Фильтрация по статусу (опционально)
        if (command.status() != null) {
            stream = stream.filter(draft -> draft.getDraftStatus() == command.status());
        }

        // Сортировка по дате создания (newest first)
        return stream
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
