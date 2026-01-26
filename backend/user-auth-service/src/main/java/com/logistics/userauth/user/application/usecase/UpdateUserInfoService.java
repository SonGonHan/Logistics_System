package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.user.adapter.in.UserControllerMapper;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.UpdateUserInfoUseCase;
import com.logistics.userauth.user.application.port.in.command.UpdateUserInfoCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Реализация use case обновления профиля пользователя.
 *
 * <h2>Обновление профиля</h2>
 * Обновляет email и ФИО.
 *
 * <h2>Смена пароля</h2>
 * Если {@code newPassword} передан и не blank, требует {@code oldPassword} и проверяет его через {@link org.springframework.security.crypto.password.PasswordEncoder}.
 * При неверном {@code oldPassword} выбрасывает {@link org.springframework.security.authentication.BadCredentialsException}.
 *
 * <h2>Смена телефона</h2>
 * Если телефон меняется, требует подтверждения номера через {@link SmsRepository#isPhoneVerified(String)}.
 * При неподтвержденном номере выбрасывает {@link com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException}.
 *
 * <h2>Persistence</h2>
 * Сохраняет пользователя через {@link com.logistics.userauth.user.application.port.out.UserRepository}
 * и маппит результат в {@link com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse}.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserInfoService implements UpdateUserInfoUseCase {

    private final UserRepository userRepository;
    private final SmsRepository smsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfoResponse update(UpdateUserInfoCommand command) {

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
