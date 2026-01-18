package com.logistics.userauth.user.application.port.in;

import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPhoneCommand;

public interface UpdateUserPhoneUseCase {
    UserInfoResponse update(UpdateUserPhoneCommand command);
}
