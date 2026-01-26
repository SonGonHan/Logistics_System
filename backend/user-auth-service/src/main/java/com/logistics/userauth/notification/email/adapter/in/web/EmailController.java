package com.logistics.userauth.notification.email.adapter.in.web;

import com.logistics.userauth.common.api.SendEmailVerificationCodeOperation;
import com.logistics.userauth.common.api.VerifyEmailOperation;
import com.logistics.userauth.notification.email.adapter.in.web.dto.EmailConfigResponse;
import com.logistics.userauth.notification.email.adapter.in.web.dto.EmailVerificationCodeRequest;
import com.logistics.userauth.notification.email.adapter.in.web.dto.VerifyEmailRequest;
import com.logistics.userauth.notification.email.adapter.in.web.dto.VerifyEmailResponse;
import com.logistics.userauth.notification.email.application.port.in.SendEmailVerificationCodeUseCase;
import com.logistics.userauth.notification.email.application.port.in.VerifyEmailUseCase;
import com.logistics.userauth.notification.email.application.port.in.command.SendEmailVerificationCodeCommand;
import com.logistics.userauth.notification.email.application.port.in.command.VerifyEmailCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для Email верификации.
 *
 * <h2>Endpoints</h2>
 * <ul>
 *   <li>GET /api/v1/email/config — Получение конфигурации email-верификации</li>
 *   <li>POST /api/v1/email/send-verification-code — Отправка Email кода</li>
 *   <li>POST /api/v1/email/verify-email — Проверка введенного кода</li>
 * </ul>
 *
 * <h2>Use Cases</h2>
 * <ul>
 *   <li>Верификация email при регистрации</li>
 *   <li>Подтверждение смены email</li>
 *   <li>Двухфакторная аутентификация через email</li>
 * </ul>
 *
 * <h2>Security</h2>
 * <ul>
 *   <li>Rate limiting (защита от спама) — реализовано в SendVerificationCodeService</li>
 *   <li>Максимум попыток ввода кода — реализовано в VerifyEmailService</li>
 *   <li>TTL кодов (5 минут) — реализовано в Redis</li>
 * </ul>
 *
 * @see SendEmailVerificationCodeUseCase
 * @see VerifyEmailUseCase
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(
        name = "Email Верификация",
        description = "Endpoints для отправки и проверки Email кодов верификации"
)
public class EmailController {

    private final SendEmailVerificationCodeUseCase sendEmailVerificationCodeUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;

    @Value("${app.email.verification.resend-cooldown-seconds}")
    private long resendCooldownSeconds;

    /**
     * GET /api/v1/email/config — получение конфигурации email-верификации.
     *
     * @return конфигурация с параметрами для фронтенда
     */
    @GetMapping("/config")
    @Operation(
            summary = "Получить конфигурацию email-верификации",
            description = "Возвращает настройки для фронтенда (cooldown период)"
    )
    public ResponseEntity<EmailConfigResponse> getEmailConfig() {
        var config = new EmailConfigResponse(resendCooldownSeconds);
        return ResponseEntity.ok(config);
    }

    /**
     * POST /api/v1/email/send-verification-code — запускает отправку Email кода.
     * <p>
     * Принимает email адрес и делегирует бизнес-логику в {@link SendEmailVerificationCodeUseCase}.
     *
     * @param request DTO с email адресом
     * @return 200 OK, если запрос успешно принят
     */
    @PostMapping("/send-verification-code")
    @SendEmailVerificationCodeOperation
    public ResponseEntity<Void> sendVerificationCode(
            @Valid @RequestBody EmailVerificationCodeRequest request
    ) {
        log.info("Sending verification code to email: {}", request.email());
        var command = new SendEmailVerificationCodeCommand(request.email());
        sendEmailVerificationCodeUseCase.sendCode(command);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/v1/email/verify-email — проверяет введённый Email код.
     * <p>
     * Принимает email и код, делегирует проверку в {@link VerifyEmailUseCase}.
     *
     * @param request DTO с email и кодом
     * @return 200 OK с флагом успешной верификации
     */
    @PostMapping("/verify-email")
    @VerifyEmailOperation
    public ResponseEntity<VerifyEmailResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) throws Throwable {
        log.info("Verifying email: {}", request.email());

        var command = VerifyEmailCommand.builder()
                .email(request.email())
                .code(request.code())
                .build();

        verifyEmailUseCase.verify(command);

        var response = VerifyEmailResponse.builder()
                .email(request.email())
                .verified(true)
                .message("Email успешно верифицирован")
                .build();

        log.info("Email verified successfully: {}", request.email());
        return ResponseEntity.ok(response);
    }
}
