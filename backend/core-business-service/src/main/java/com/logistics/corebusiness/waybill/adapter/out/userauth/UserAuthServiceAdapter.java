package com.logistics.corebusiness.waybill.adapter.out.userauth;

import com.logistics.corebusiness.waybill.application.port.out.RecipientUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Адаптер: вызывает user-auth-service для поиска или создания получателя по телефону.
 *
 * <p>Пробрасывает JWT текущего пользователя, чтобы user-auth-service
 * мог пройти аутентификацию.
 */
@Component
@RequiredArgsConstructor
public class UserAuthServiceAdapter implements RecipientUserPort {

    private final RestTemplate restTemplate;

    @Value("${services.user-auth.url}")
    private String userAuthUrl;

    @Override
    public Long findOrCreateByPhone(String phone) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(extractCurrentJwt());

        var entity = new HttpEntity<>(new EnsureByPhoneRequest(phone), headers);

        var response = restTemplate.exchange(
                userAuthUrl + "/users/ensure-by-phone",
                HttpMethod.POST,
                entity,
                EnsureByPhoneResponse.class
        );

        if (response.getBody() == null) {
            throw new IllegalStateException("user-auth-service вернул пустой ответ");
        }
        return response.getBody().userId();
    }

    private String extractCurrentJwt() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        throw new IllegalStateException("JWT аутентификация не найдена в SecurityContext");
    }

    record EnsureByPhoneRequest(String phone) {}
    record EnsureByPhoneResponse(Long userId) {}
}