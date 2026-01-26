package com.logistics.userauth.user.application.usecase;

import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.auth.jwt.application.exception.PhoneNotVerifiedException;
import com.logistics.userauth.notification.sms.application.port.out.SmsRepository;
import com.logistics.userauth.user.adapter.in.UserControllerMapper;
import com.logistics.userauth.user.adapter.in.web.dto.UserInfoResponse;
import com.logistics.userauth.user.application.port.in.UpdateUserPhoneUseCase;
import com.logistics.userauth.user.application.port.in.command.UpdateUserPhoneCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UpdateUserPhoneService implements UpdateUserPhoneUseCase {

    private final UserRepository userRepository;
    private final SmsRepository smsRepository;

    @Override
    public UserInfoResponse update(UpdateUserPhoneCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var normalizedPhone = PhoneUtils.normalize(command.phone());
        if (normalizedPhone == null || normalizedPhone.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone is required");
        }

        if (normalizedPhone.equals(user.getPhone())) {
            return UserControllerMapper.toResponse(user);
        }

        if (!smsRepository.isVerified(normalizedPhone)) {
            throw new PhoneNotVerifiedException("Phone is not verified");
        }

        user.setPhone(normalizedPhone);
        smsRepository.deleteVerificationStatus(normalizedPhone);

        var saved = userRepository.save(user);
        return UserControllerMapper.toResponse(saved);
    }
}
