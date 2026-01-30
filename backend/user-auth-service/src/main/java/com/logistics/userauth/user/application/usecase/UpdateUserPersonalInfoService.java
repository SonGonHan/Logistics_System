package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.audit.application.port.in.CreateAuditLogUseCase;
import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;
import com.logistics.userauth.user.adapter.in.UserControllerMapper;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.UpdateUserPersonalInfoUseCase;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPersonalInfoCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateUserPersonalInfoService implements UpdateUserPersonalInfoUseCase {

    private final UserRepository userRepository;
    private final CreateAuditLogUseCase createAuditLogUseCase;

    @Override
    public UserInfoResponse update(UpdateUserPersonalInfoCommand command) {

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEmail(command.email());
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setMiddleName(command.middleName());

        var saved = userRepository.save(user);

        // Audit: USER_UPDATE
        List<String> updatedFields = new ArrayList<>();
        Map<String, Object> newValues = new HashMap<>();

        if (command.email() != null) {
            updatedFields.add("email");
            newValues.put("email", saved.getEmail());
        }
        if (command.firstName() != null) {
            updatedFields.add("firstName");
            newValues.put("firstName", saved.getFirstName());
        }
        if (command.lastName() != null) {
            updatedFields.add("lastName");
            newValues.put("lastName", saved.getLastName());
        }
        if (command.middleName() != null) {
            updatedFields.add("middleName");
            newValues.put("middleName", saved.getMiddleName());
        }

        newValues.put("updatedFields", updatedFields);

        createAuditLogUseCase.create(new CreateAuditLogCommand(
                saved.getId(),
                "USER_UPDATE",
                saved.getPhone(),
                command.ipAddress(),
                command.userAgent(),
                newValues,
                "users",
                saved.getId()
        ));

        return UserControllerMapper.toResponse(saved);
    }
}
