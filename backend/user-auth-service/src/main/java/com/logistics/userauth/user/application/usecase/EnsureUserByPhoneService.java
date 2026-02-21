package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.application.port.in.EnsureUserByPhoneUseCase;
import com.logistics.userauth.user.application.port.in.command.EnsureUserByPhoneCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import com.logistics.userauth.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Сервис: найти или создать пользователя по номеру телефона.
 *
 * <p>Вызывается из core-business-service при создании накладной,
 * когда оператор вводит телефон получателя.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EnsureUserByPhoneService implements EnsureUserByPhoneUseCase {

    private final UserRepository userRepository;

    @Override
    public Long ensure(EnsureUserByPhoneCommand command) {
        if (userRepository.findByPhone(command.phone()).isEmpty()) {
            var user = User.builder()
                    .phone(command.phone())
                    .role(UserRole.CLIENT)
                    .status(UserStatus.ACTIVE)
                    .createdTime(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        }
        return userRepository.findByPhone(command.phone()).get().getId();

    }
}