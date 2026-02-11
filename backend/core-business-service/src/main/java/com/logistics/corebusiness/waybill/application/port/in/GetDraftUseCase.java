package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.GetDraftCommand;

public interface GetDraftUseCase {
    DraftResponse get(GetDraftCommand command);
}
