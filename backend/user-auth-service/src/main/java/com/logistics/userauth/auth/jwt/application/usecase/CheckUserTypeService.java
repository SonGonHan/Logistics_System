package com.logistics.userauth.auth.jwt.application.usecase;

import com.logistics.shared.utils.PhoneUtils;
import com.logistics.userauth.auth.jwt.adapter.in.web.dto.CheckUserTypeResponse;
import com.logistics.userauth.auth.jwt.application.port.in.CheckUserTypeUseCase;
import com.logistics.userauth.auth.jwt.application.port.in.command.CheckUserTypeCommand;
import com.logistics.userauth.user.application.port.out.UserRepository;
import com.logistics.userauth.user.domain.User;
import com.logistics.userauth.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Сервис для определения типа пользователя перед авторизацией.
 *
 * <h2>Алгоритм работы</h2>
 * <ol>
 *   <li>Нормализует identifier (удаляет пробелы, форматирует телефон)</li>
 *   <li>Ищет пользователя сначала по телефону, затем по email</li>
 *   <li>Определяет тип пользователя:
 *     <ul>
 *       <li>Найден + CLIENT → {userExists: true, isClient: true}</li>
 *       <li>Найден + другая роль → {userExists: true, isClient: false}</li>
 *       <li>Не найден → {userExists: false, isClient: true}</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h2>Автоопределение формата</h2>
 * Identifier может быть:
 * - Телефоном: +79991234567, 79991234567, +7 (999) 123-45-67
 * - Email: user@example.com
 *
 * Система пытается найти по обоим форматам.
 *
 * @see CheckUserTypeUseCase для контракта
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckUserTypeService implements CheckUserTypeUseCase {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CheckUserTypeResponse check(CheckUserTypeCommand command) {
        log.info("Checking user type for identifier: {}", maskIdentifier(command.identifier()));

        // Пытаемся найти пользователя
        Optional<User> userOpt = findUserByIdentifier(command.identifier());

        if (userOpt.isEmpty()) {
            log.info("User not found, will be registered as CLIENT");
            return CheckUserTypeResponse.builder()
                    .userExists(false)
                    .isClient(false)
                    .build();
        }

        var user = userOpt.get();
        var isClient = user.getRole() == UserRole.CLIENT;

        log.info("User found with role: {}, isClient: {}", user.getRole(), isClient);

        return CheckUserTypeResponse.builder()
                .userExists(true)
                .isClient(isClient)
                .build();
    }

    /**
     * Ищет пользователя по идентификатору (телефон или email).
     *
     * @param identifier Телефон или email
     * @return Optional с пользователем, если найден
     */
    private Optional<User> findUserByIdentifier(String identifier) {
        // Сначала пытаемся найти по телефону
        String normalized = PhoneUtils.normalize(identifier);
        Optional<User> userOpt = userRepository.findByPhone(normalized);

        // Если не найден, пытаемся по email
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(identifier.trim().toLowerCase());
        }

        return userOpt;
    }

    /**
     * Маскирует identifier для логирования (безопасность).
     *
     * @param identifier Телефон или email
     * @return Замаскированная строка
     */
    private String maskIdentifier(String identifier) {
        if (identifier == null || identifier.length() < 4) {
            return "***";
        }
        if (identifier.contains("@")) {
            // Email: u***@example.com
            int atIndex = identifier.indexOf('@');
            return identifier.charAt(0) + "***" + identifier.substring(atIndex);
        } else {
            // Phone: +799912***67
            int len = identifier.length();
            return identifier.substring(0, Math.min(6, len - 4)) + "***" + identifier.substring(len - 2);
        }
    }
}