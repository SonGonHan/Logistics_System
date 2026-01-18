package com.logistics.userauth.user.adapter.in.web;

import com.logistics.userauth.common.api.GetInfoOperation;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.adapter.in.web.dto.UserPasswordUpdateRequest;
import com.logistics.userauth.user.adapter.in.web.dto.UserPersonalDataUpdateRequest;
import com.logistics.userauth.user.adapter.in.web.dto.UserPhoneUpdateRequest;
import com.logistics.userauth.user.application.port.in.*;
import com.logistics.userauth.user.application.port.in.command.*;
import com.logistics.userauth.user.infrastructure.LogisticsUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST контроллер для работы с данными текущего пользователя (endpoint'ы вида {@code /users/me}).
 *
 * <h2>Назначение</h2>
 * Предоставляет доступ к профилю текущего пользователя и его обновлению, используя контекст аутентификации Spring Security.
 *
 * <h2>Архитектура</h2>
 * Контроллер является thin-controller: не содержит бизнес-логики, а формирует команды и делегирует выполнение
 * в application layer через {@link com.logistics.userauth.user.application.port.in.GetUserInfoUseCase}
 * и {@link com.logistics.userauth.user.application.port.in.UpdateUserInfoUseCase}.
 *
 * <h2>Security</h2>
 * Идентификатор пользователя извлекается из {@link com.logistics.userauth.user.infrastructure.LogisticsUserDetails}
 * (principal в {@link org.springframework.security.core.Authentication}).
 */
@Tag(
        name = "Пользователи",
        description = "REST API endpoints для взаимодействия с информацией о пользователе, логина, refresh токенов и выхода из системы"
)
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserInfoUseCase getUserInfoUseCase;
    private final UpdateUserPhoneUseCase updateUserPhoneUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final UpdateUserPersonalInfoUseCase updateUserPersonalInfoUseCase;

    /**
     * Возвращает информацию о текущем пользователе.
     *
     * @param authentication контекст аутентификации, содержащий principal типа {@link com.logistics.userauth.user.infrastructure.LogisticsUserDetails}.
     * @return HTTP 200 с {@link com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse}.
     * @throws ClassCastException если principal имеет неожидаемый тип.
     */
    @GetMapping("/me")
    @GetInfoOperation
    public ResponseEntity<UserInfoResponse> getInfo(Authentication authentication) {
        var principal = (LogisticsUserDetails) authentication.getPrincipal();
        var command = new GetUserInfoCommand(principal.getId());
        return ResponseEntity.ok(getUserInfoUseCase.getUserInfo(command));
    }

    @PutMapping("/me/phone")
    public ResponseEntity<UserInfoResponse> updatePhone(
            Authentication authentication,
            @Valid @RequestBody UserPhoneUpdateRequest request
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        var userId = ((LogisticsUserDetails) authentication.getPrincipal()).getId();

        var command = UpdateUserPhoneCommand.builder()
                .userId(userId)
                .phone(request.phone())
                .build();

        return ResponseEntity.ok(updateUserPhoneUseCase.update(command));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            Authentication authentication,
            @Valid @RequestBody UserPasswordUpdateRequest request
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        var userId = ((LogisticsUserDetails) authentication.getPrincipal()).getId();

        var command = UpdateUserPasswordCommand.builder()
                .userId(userId)
                .oldPassword(request.oldPassword())
                .newPassword(request.newPassword())
                .build();

        updateUserPasswordUseCase.update(command);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/personal")
    public ResponseEntity<UserInfoResponse> updatePersonalInfo(
            Authentication authentication,
            @Valid @RequestBody UserPersonalDataUpdateRequest request
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        var userId = ((LogisticsUserDetails) authentication.getPrincipal()).getId();

        var command = UpdateUserPersonalInfoCommand.builder()
                .userId(userId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .email(request.email())
                .build();

        return ResponseEntity.ok(updateUserPersonalInfoUseCase.update(command));
    }


}
