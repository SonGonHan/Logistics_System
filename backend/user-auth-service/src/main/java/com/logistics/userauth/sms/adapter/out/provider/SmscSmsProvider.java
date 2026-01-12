package com.logistics.userauth.sms.adapter.out.provider;

import com.logistics.userauth.sms.application.port.out.SendSmsPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Реализация отправки SMS через SMSC.ru API (российский провайдер).
 *
 * <h2>Регистрация SMSC.ru</h2>
 * <ol>
 *   <li>Зарегистрируйтесь на https://smsc.ru/register/</li>
 *   <li>Пополните баланс (минимум 300 руб)</li>
 *   <li>Получите логин и пароль из личного кабинета</li>
 *   <li>Установите переменные окружения SMSC_LOGIN, SMSC_PASSWORD</li>
 * </ol>
 *
 * <p><b>Стоимость:</b> ~2-4 руб за SMS по России</p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "smsc")
public class SmscSmsProvider implements SendSmsPort {
    private static final String SMSC_API_URL = "https://smsc.ru/sys/send.php";

    @Value("${app.sms.smsc.login}")
    private String login;

    @Value("${app.sms.smsc.password}")
    private String password;

    @Value("${app.sms.smsc.sender}")
    private String sender;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean sendVerificationCode(String phone, String code) {
        try {
             var message = String.format("Ваш код: %s. Действителен 5 минут.", code);

            var url = UriComponentsBuilder.fromHttpUrl(SMSC_API_URL)
                    .queryParam("login", login)
                    .queryParam("psw", password)
                    .queryParam("phones", phone)
                    .queryParam("mes", message)
                    .queryParam("sender", sender)
                    .queryParam("charset", "utf-8")
                    .queryParam("fmt", "3")  // JSON ответ
                    .toUriString();

            var response = restTemplate.getForObject(url, String.class);
            log.info("SMS sent to {} via SMSC. Response: {}", phone, response);

            return response != null && !response.contains("\"error\"");

        } catch (Exception e) {
            log.error("Failed to send SMS via SMSC to {}: {}", phone, e.getMessage());
            return false;
        }
    }
}
