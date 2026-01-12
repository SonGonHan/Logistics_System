package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.adapter.in.UserControllerMapper;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.GetUserInfoUseCase;
import com.logistics.userauth.user.application.port.in.command.GetUserInfoCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Реализация use case получения информации о пользователе.
 *
 * <p>Загружает пользователя из {@link com.logistics.userauth.user.application.port.out.UserRepository}
 * и преобразует доменную модель в DTO ответа через {@link com.logistics.userauth.user.adapter.in.UserControllerMapper}.
 *
 * <p>Если пользователь не найден, выбрасывает {@link org.springframework.web.server.ResponseStatusException}
 * со статусом 404.
 */
@Service
@RequiredArgsConstructor
public class GetUserInfoService implements GetUserInfoUseCase {

    private final UserRepository repository;

    @Override
    public UserInfoResponse getUserInfo(GetUserInfoCommand command) {
        Long userId = command.userId();

        var actualUser = repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserControllerMapper.toResponse(actualUser);    }
}
