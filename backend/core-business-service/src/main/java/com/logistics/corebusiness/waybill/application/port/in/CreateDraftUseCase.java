package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.application.port.in.command.CreateDraftCommand;

public interface CreateDraftUseCase {
    void create (CreateDraftCommand command);
}
