package com.logistics.userauth.user.application.port.in;

import com.logistics.userauth.user.application.port.in.command.UpdateUserPasswordCommand;

public interface UpdateUserPasswordUseCase {
    void update(UpdateUserPasswordCommand command);
}
