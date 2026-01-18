package com.logistics.userauth.user.application.usecase;

import com.logistics.userauth.user.application.port.in.UpdateUserPasswordUseCase;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPasswordCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UpdateUserPasswordService implements UpdateUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void update(UpdateUserPasswordCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (command.oldPassword() == null || command.oldPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Введите старый пароль");
        }

        if (command.newPassword() == null || command.newPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Введите новый пароль");
        }

        if (!passwordEncoder.matches(command.oldPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Неверный старый пароль");
        }

        user.setPasswordHash(passwordEncoder.encode(command.newPassword()));
        userRepository.save(user);
    }
}
