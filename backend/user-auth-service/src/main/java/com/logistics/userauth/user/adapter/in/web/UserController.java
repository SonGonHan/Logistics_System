package com.logistics.userauth.user.adapter.in.web;

import com.logistics.userauth.common.api.GetInfoOperation;
import com.logistics.userauth.common.api.UpdateInfoOperation;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.adapter.in.web.dto.UserUpdateRequest;
import com.logistics.userauth.user.application.port.in.GetUserInfoUseCase;
import com.logistics.userauth.user.application.port.in.UpdateUserInfoUseCase;
import com.logistics.userauth.user.application.port.in.command.GetUserInfoCommand;
import com.logistics.userauth.user.application.port.in.command.UpdateUserInfoCommand;
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
    private final UpdateUserInfoUseCase updateUserInfoUseCase;

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

    /**
     * Обновляет данные профиля текущего пользователя.
     *
     * <p>Дополнительно поддерживает смену пароля: если передан {@code newPassword},
     * то требуется {@code oldPassword} и выполняется проверка через {@code PasswordEncoder.matches}.
     *
     * @param authentication контекст аутентификации; если отсутствует — запрос считается неаутентифицированным.
     * @param request входной DTO для обновления профиля (валидируется через Bean Validation).
     * @return HTTP 200 с обновлёнными данными пользователя ({@link com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse}).
     * @throws org.springframework.web.server.ResponseStatusException со статусом 401, если пользователь не аутентифицирован.
     */
    @PostMapping("/me")
    @UpdateInfoOperation
    public ResponseEntity<UserInfoResponse> updateInfo(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Long userId = ((LogisticsUserDetails) authentication.getPrincipal()).getId();

        var command = UpdateUserInfoCommand.builder()
                .userId(userId)
                .email(request.email())
                .phone(request.phone())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .middleName(request.middleName())
                .newPassword(request.newPassword())
                .oldPassword(request.oldPassword())
                .build();
        return ResponseEntity.ok(updateUserInfoUseCase.update(command));
    }

}
