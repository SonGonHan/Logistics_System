package com.logistics.userauth.notification.email.adapter.out.provider;

import com.logistics.userauth.notification.email.application.port.out.SendEmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Реализация отправки Email через SMTP (Spring Mail).
 *
 * <h2>Настройка SMTP</h2>
 * <p>Требуется настроить SMTP сервер в application.yml:</p>
 * <pre>
 * spring:
 *   mail:
 *     host: smtp.gmail.com
 *     port: 587
 *     username: your-email@gmail.com
 *     password: your-app-password
 *     properties:
 *       mail.smtp.auth: true
 *       mail.smtp.starttls.enable: true
 * </pre>
 *
 * <h2>Gmail App Password</h2>
 * <ol>
 *   <li>Включите 2FA на аккаунте Google</li>
 *   <li>Перейдите в https://myaccount.google.com/apppasswords</li>
 *   <li>Создайте App Password для приложения</li>
 *   <li>Используйте сгенерированный пароль вместо обычного</li>
 * </ol>
 *
 * <h2>Другие провайдеры</h2>
 * <ul>
 *   <li><b>Yandex:</b> smtp.yandex.ru:465 (SSL)</li>
 *   <li><b>Mail.ru:</b> smtp.mail.ru:465 (SSL)</li>
 *   <li><b>Outlook:</b> smtp-mail.outlook.com:587 (TLS)</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.email.provider", havingValue = "smtp")
public class SmtpEmailProvider implements SendEmailPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.smtp.sender-name:Logistics System}")
    private String senderName;

    @Override
    public boolean sendVerificationCode(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(email);
            helper.setSubject("Код верификации");
            helper.setText(buildEmailContent(code), true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", email);
            return true;

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while sending email to {}: {}", email, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Создает HTML содержимое письма с кодом верификации.
     *
     * @param code код верификации
     * @return HTML строка
     */
    private String buildEmailContent(String code) {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Код верификации</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td style="padding: 40px; text-align: center;">
                                            <h1 style="color: #333333; margin: 0 0 20px 0;">Код верификации</h1>
                                            <p style="color: #666666; font-size: 16px; line-height: 1.5; margin: 0 0 30px 0;">
                                                Ваш код для подтверждения email:
                                            </p>
                                            <div style="background-color: #f8f9fa; border: 2px dashed #dee2e6; border-radius: 8px; padding: 20px; margin: 0 0 30px 0;">
                                                <span style="font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #007bff;">%s</span>
                                            </div>
                                            <p style="color: #999999; font-size: 14px; line-height: 1.5; margin: 0;">
                                                Код действителен в течение <strong>5 минут</strong>.<br>
                                                Если вы не запрашивали этот код, проигнорируйте это письмо.
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;">
                                            <p style="color: #999999; font-size: 12px; margin: 0;">
                                                © 2026 Logistics System. Все права защищены.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(code);
    }
}