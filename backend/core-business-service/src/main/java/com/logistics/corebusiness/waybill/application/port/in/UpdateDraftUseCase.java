package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.UpdateDraftCommand;

public interface UpdateDraftUseCase {
    DraftResponse update(UpdateDraftCommand command);
}
