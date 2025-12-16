package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.userauth.auth.jwt.application.exception.InvalidRefreshTokenException;
import com.logistics.userauth.auth.jwt.application.port.in.RevokeRefreshTokenUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.RevokeRefreshTokenCommand;
import com.logistics.userauth.auth.session.application.port.out.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Сервис для отзыва (revoke) refresh токенов.
 *
 * <h2>Назначение</h2>
 * Отзыв токена делает его непригодным для дальнейшего использования.
 * Используется при logout и других операциях завершения сессии.
 *
 * <h2>Процесс</h2>
 * 1. Находит сессию по refresh token
 * 2. Помечает сессию как revoked = true
 * 3. Сохраняет в БД
 *
 * При попытке использовать отозванный токен для обновления access token
 * будет выброшено исключение InvalidRefreshTokenException.
 *
 * <h2>Исключения</h2>
 * - InvalidRefreshTokenException: Если токен не найден в БД
 *
 * @implements RevokeRefreshTokenUseCase
 */
@Service
@RequiredArgsConstructor
public class RevokeRefreshTokenService implements RevokeRefreshTokenUseCase {

    private final UserSessionRepository repository;

    /**
     * Отзывает refresh token (logout), помечая соответствующую сессию как revoked=true.
     *
     * <p>После выполнения:</p>
     * <ul>
     *   <li>refresh token больше не может быть использован для обновления access token;</li>
     *   <li>уже выданный access token продолжит действовать до истечения TTL (если не применяются дополнительные механизмы блокировки).</li>
     * </ul>
     *
     * @param command Команда отзыва refresh token.
     * @throws InvalidRefreshTokenException
     *         Если refresh token не найден (или сессия недоступна).
     * @see RevokeRefreshTokenUseCase
     */
    @Override
    public void revoke(RevokeRefreshTokenCommand command) {
        var session = repository.findByRefreshToken(command.refreshToken()).orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        session.setRevoked(true);
        repository.save(session);
    }
}
