package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.application.port.in.command.DeleteDraftCommand;

public interface DeleteDraftUseCase {
    void delete(DeleteDraftCommand command);
}
