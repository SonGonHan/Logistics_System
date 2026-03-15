package com.logistics.corebusiness.acceptance.application.port.in;

import com.logistics.corebusiness.acceptance.adapter.in.web.dto.AcceptanceResponse;
import com.logistics.corebusiness.acceptance.application.port.in.command.StartAcceptanceCommand;

/**
 * Use Case начала приёмки посылки на ПВЗ.
 */
public interface StartAcceptanceUseCase {

    /**
     * Начинает процесс приёмки по штрих-коду черновика.
     *
     * @param command входные данные операции
     * @return данные найденного черновика
     */
    AcceptanceResponse start(StartAcceptanceCommand command);
}
