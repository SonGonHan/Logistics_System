package com.logistics.corebusiness.acceptance.application.port.in;

import com.logistics.corebusiness.acceptance.adapter.in.web.dto.CompleteAcceptanceResponse;
import com.logistics.corebusiness.acceptance.application.port.in.command.CompleteAcceptanceCommand;

/**
 * Use Case завершения приёмки посылки на ПВЗ.
 */
public interface CompleteAcceptanceUseCase {

    /**
     * Завершает приёмку и создаёт накладную на основе черновика.
     *
     * @param command входные данные операции
     * @return данные созданной накладной
     */
    CompleteAcceptanceResponse complete(CompleteAcceptanceCommand command);
}
