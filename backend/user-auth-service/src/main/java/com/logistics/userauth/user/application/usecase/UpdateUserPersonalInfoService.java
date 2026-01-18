package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.adapter.in.UserControllerMapper;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.UpdateUserPersonalInfoUseCase;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPersonalInfoCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UpdateUserPersonalInfoService implements UpdateUserPersonalInfoUseCase {

    private final UserRepository userRepository;

    @Override
    public UserInfoResponse update(UpdateUserPersonalInfoCommand command) {

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEmail(command.email());
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setMiddleName(command.middleName());

        var saved = userRepository.save(user);

        return UserControllerMapper.toResponse(saved);
    }
}
