package com.logistics.userauth.sms.adapter.in.web;

import com.logistics.userauth.common.api.SendVerificationCodeOperation;
import com.logistics.userauth.common.api.VerifyPhoneOperation;
import com.logistics.userauth.sms.adapter.in.web.dto.SendVerificationCodeRequest;
import com.logistics.userauth.sms.adapter.in.web.dto.VerifyPhoneRequest;
import com.logistics.userauth.sms.adapter.in.web.dto.VerifyPhoneResponse;
import com.logistics.userauth.sms.application.port.in.SendVerificationCodeUseCase;
import com.logistics.userauth.sms.application.port.in.VerifyPhoneUseCase;
import com.logistics.userauth.sms.application.port.in.command.SendVerificationCodeCommand;
import com.logistics.userauth.sms.application.port.in.command.VerifyPhoneCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для SMS верификации.
 *
 * <h2>Endpoints</h2>
 * <ul>
 *   <li>POST /api/v1/sms/send-verification-code — Отправка SMS кода</li>
 *   <li>POST /api/v1/sms/verify-phone — Проверка введенного кода</li>
 * </ul>
 *
 * <h2>Use Cases</h2>
 * <ul>
 *   <li>Верификация телефона при регистрации</li>
 *   <li>Двухфакторная аутентификация (2FA)</li>
 * </ul>
 *
 * <h2>Security</h2>
 * <ul>
 *   <li>Rate limiting (60 секунд между отправками) — реализовано в SendVerificationCodeService</li>
 *   <li>Максимум 3 попытки ввода кода — реализовано в VerifyPhoneService</li>
 * </ul>
 *
 * @see SendVerificationCodeUseCase
 * @see VerifyPhoneUseCase
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
@Tag(
        name = "SMS Верификация",
        description = "Endpoints для отправки и проверки SMS кодов верификации"
)
public class SmsController {

    private final SendVerificationCodeUseCase sendVerificationCodeUseCase;
    private final VerifyPhoneUseCase verifyPhoneUseCase;

    /**
     * POST /api/v1/sms/send-verification-code — запускает отправку SMS кода на телефон.
     * <p>
     * Принимает номер телефона и делегирует бизнес-логику в {@link SendVerificationCodeUseCase}.
     *
     * @param request DTO с номером телефона
     * @return 200 OK, если запрос успешно принят
     */
    @PostMapping("/send-verification-code")
    @SendVerificationCodeOperation
    public ResponseEntity<Void> sendVerificationCode(
            @Valid @RequestBody SendVerificationCodeRequest request
    ) {
        log.info("Sending verification code to phone: {}", request.phone());
        var command = new SendVerificationCodeCommand(request.phone());
        sendVerificationCodeUseCase.sendCode(command);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/v1/sms/verify-phone — проверяет введённый SMS код.
     * <p>
     * Принимает телефон и код, делегирует проверку в {@link VerifyPhoneUseCase}.
     *
     * @param request DTO с телефоном и кодом
     * @return 200 OK с флагом успешной верификации
     */
    @PostMapping("/verify-phone")
    @VerifyPhoneOperation
    public ResponseEntity<VerifyPhoneResponse> verifyPhone(
            @Valid @RequestBody VerifyPhoneRequest request
    ) {
        log.info("Verifying phone: {}", request.phone());

        var command = VerifyPhoneCommand.builder()
                .phone(request.phone())
                .code(request.code())
                .build();

        verifyPhoneUseCase.verify(command);

        var response = VerifyPhoneResponse.builder()
                .phone(request.phone())
                .verified(true)
                .message("Телефон успешно верифицирован")
                .build();

        log.info("Phone verified successfully: {}", request.phone());
        return ResponseEntity.ok(response);
    }
}
