package com.logistics.userauth.auth.jwt.application.port.in.command;

import com.logistics.userauth.auth.jwt.adapter.in.web.AuthController;
import com.logistics.userauth.auth.jwt.application.port.in.RegisterUserUseCase;
import com.logistics.userauth.auth.jwt.application.usecase.RegisterUserService;
import com.logistics.userauth.user.adapter.in.web.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;

/**
 * Команда для регистрации нового пользователя в системе.
 *
 * Инкапсулирует все данные, необходимые для создания нового аккаунта:
 * - email: адрес электронной почты
 * - phone: номер телефона (уникальный идентификатор)
 * - rawPassword: пароль в открытом виде (будет захеширован BCrypt)
 * - firstName, lastName, middleName: ФИО пользователя
 * - ipAddress: IP-адрес для логирования аудита
 * - userAgent: информация об устройстве для логирования
 *
 * Валидация:
 * - phone: должен пройти проверку @Phone (РФ, Беларусь, Казахстан)
 * - rawPassword: должен пройти проверку @Password (8+ символов, сложность)
 * - email: должен быть валидным форматом email
 *
 * Используется в: RegisterUserService
 * Создается в: AuthController из SignUpRequest
 *
 * Пример создания:
 * ```java
 * RegisterUserCommand command = RegisterUserCommand.builder()
 *     .email("ivan@example.com")
 *     .phone("+79991234567")
 *     .rawPassword("Password123!")
 *     .firstName("Иван")
 *     .lastName("Иванов")
 *     .middleName("Иванович")
 *     .ipAddress("192.168.1.1")
 *     .userAgent("Mozilla/5.0")
 *     .build();
 * ```
 *
 * @see RegisterUserUseCase
 * @see RegisterUserService
 * @see AuthController#signUp(SignUpRequest, HttpServletRequest)
 */
@Builder
public record RegisterUserCommand(
        String email,
        String phone,
        String rawPassword,
        String firstName,
        String lastName,
        String middleName,
        String ipAddress,
        String userAgent
) { }
