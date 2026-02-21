package com.logistics.corebusiness.waybill.application.port.in;

import com.logistics.corebusiness.waybill.adapter.in.web.dto.DraftResponse;
import com.logistics.corebusiness.waybill.application.port.in.command.GetUserDraftListCommand;

import java.util.List;

public interface GetUserDraftListUseCase {
    List<DraftResponse> get(GetUserDraftListCommand command);
}
