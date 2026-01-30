# План Имплементации: Интеграция Аудит Логирования для UserAuthService

## Обзор

Интегрировать аудит логирование в существующие операции UserAuthService для отслеживания событий аутентификации, действий управления пользователями и жизненного цикла сессий. Инфраструктура аудита (domain, repository, database) уже существует, но не используется в коде.

## Scope (Область применения)

### Включено
- **Операции аутентификации (8)**: USER_REGISTER, USER_LOGIN_SUCCESS, USER_LOGIN_FAILURE, USER_LOGOUT, PASSWORD_CHANGE, SESSION_CREATE, SESSION_REVOKE, TOKEN_REFRESH
- **Операции управления пользователями (2)**: USER_UPDATE (UpdateUserInfoService, UpdateUserPersonalInfoService)

### Не включено (на потом)
- Фронтенд верификации email (опциональная задача)
- Аудит логирование через AOP
- Асинхронное аудит логирование
- Retention/архивация audit logs
- Admin UI для просмотра audit logs

## Архитектурные Решения

### 1. Стратегия внедрения: Прямая интеграция в Use Case
- Добавить зависимость `CreateAuditLogUseCase` в существующие сервисы
- Вызывать аудит логирование после успешных операций
- Обоснование: принцип Clean Architecture, нет инфраструктуры AOP, явное и тестируемое

### 2. Создать специализированный сервис аудита
- Создать интерфейс `CreateAuditLogUseCase` и имплементацию `CreateAuditLogService`
- Централизует логику создания audit log (валидация, defaults, timestamp)
- Следует существующему паттерну Ports & Adapters

### 3. Обработка неудачных входов
- Создать кастомное исключение `AuthenticationFailedException`, которое extends `BadCredentialsException`
- Несет phone, ipAddress, userAgent для аудит логирования
- Модифицировать `GlobalExceptionHandler` для логирования USER_LOGIN_FAILURE перед возвратом ошибки

### 4. Загрузка AuditActionType
- Runtime lookup по actionName через `AuditActionTypeService`
- Кэшировать action types в `@PostConstruct` чтобы избежать повторных запросов к БД
- Fail fast при старте если action types отсутствуют

## Шаги Имплементации

### Фаза 1: Фундамент

#### 1. Создать CreateAuditLogCommand
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/port/in/command/CreateAuditLogCommand.java`

```java
package com.logistics.userauth.audit.application.port.in.command;

import java.util.Map;

public record CreateAuditLogCommand(
    Long userId,              // Может быть null для failed login
    String actionTypeName,    // например, "USER_LOGIN_SUCCESS"
    String actorIdentifier,   // email или phone
    String ipAddress,         // из request
    String userAgent,         // из request (опционально)
    Map<String, Object> newValues,  // JSONB данные
    String tableName,         // опционально, например "users"
    Long recordId             // опционально, ID затронутой записи
) {}
```

#### 2. Создать интерфейс CreateAuditLogUseCase
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/port/in/CreateAuditLogUseCase.java`

```java
package com.logistics.userauth.audit.application.port.in;

import com.logistics.userauth.audit.application.port.in.command.CreateAuditLogCommand;

public interface CreateAuditLogUseCase {
    void create(CreateAuditLogCommand command);
}
```

#### 3. Создать имплементацию CreateAuditLogService
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/usecase/CreateAuditLogService.java`

Ключевые обязанности:
- Inject `AuditLogRepository`, `AuditActionTypeService`, `UserRepository`
- Кэшировать action types в `@PostConstruct` для производительности
- Строить доменный объект `AuditLog` из command
- Конвертировать IP string в `Inet` тип
- Обрабатывать null userId (для failed logins)
- Обернуть в try-catch чтобы ошибки аудита не ломали бизнес-логику
- Логировать ошибки аудита но не throw'ить

Action types для кэширования:
```java
List.of(
    "USER_REGISTER", "USER_LOGIN_SUCCESS", "USER_LOGIN_FAILURE",
    "USER_LOGOUT", "PASSWORD_CHANGE", "SESSION_CREATE",
    "SESSION_REVOKE", "TOKEN_REFRESH", "USER_UPDATE"
)
```

#### 4. Создать AuthenticationFailedException
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/exception/AuthenticationFailedException.java`

```java
package com.logistics.userauth.auth.jwt.application.exception;

import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;

@Getter
public class AuthenticationFailedException extends BadCredentialsException {
    private final String attemptedPhone;
    private final String ipAddress;
    private final String userAgent;

    public AuthenticationFailedException(String phone, String ip, String ua) {
        super("Неверный телефон или пароль");
        this.attemptedPhone = phone;
        this.ipAddress = ip;
        this.userAgent = ua;
    }
}
```

#### 5. Создать package-info файлы
- `backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/port/in/package-info.java`
- `backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/port/in/command/package-info.java`
- `backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/usecase/package-info.java`

### Фаза 2: Операции аутентификации

#### 6. RegisterUserService - USER_REGISTER
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RegisterUserService.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор
- После строки 88 (`var saved = userRepository.save(user);`), добавить:

```java
// Audit: USER_REGISTER
createAuditLogUseCase.create(new CreateAuditLogCommand(
    saved.getId(),
    "USER_REGISTER",
    saved.getPhone(),
    command.ipAddress(),
    command.userAgent(),
    Map.of(
        "email", saved.getEmail(),
        "phone", saved.getPhone(),
        "role", saved.getRole().name(),
        "firstName", saved.getFirstName(),
        "lastName", saved.getLastName()
    ),
    "users",
    saved.getId()
));
```

#### 7. AuthenticateUserService - USER_LOGIN_SUCCESS и failure
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/AuthenticateUserService.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор
- Строка 67: Изменить `throw new BadCredentialsException(...)` на `throw new AuthenticationFailedException(normalizedPhone, command.ipAddress(), command.userAgent())`
- Строка 70: То же изменение для несовпадения пароля
- После успешной аутентификации (строка 73+), добавить:

```java
// Audit: USER_LOGIN_SUCCESS
createAuditLogUseCase.create(new CreateAuditLogCommand(
    user.getId(),
    "USER_LOGIN_SUCCESS",
    user.getPhone(),
    command.ipAddress(),
    command.userAgent(),
    Map.of("userId", user.getId()),
    null,
    null
));
```

#### 8. GlobalExceptionHandler - USER_LOGIN_FAILURE
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/common/web/GlobalExceptionHandler.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор (используя @RequiredArgsConstructor)
- Добавить новый handler для `AuthenticationFailedException`:

```java
@ExceptionHandler(AuthenticationFailedException.class)
public ResponseEntity<Map<String, Object>> handleAuthenticationFailed(
        AuthenticationFailedException ex) {

    // Audit: USER_LOGIN_FAILURE
    createAuditLogUseCase.create(new CreateAuditLogCommand(
        null, // Неизвестный пользователь
        "USER_LOGIN_FAILURE",
        ex.getAttemptedPhone(),
        ex.getIpAddress(),
        ex.getUserAgent(),
        Map.of(
            "attemptedPhone", ex.getAttemptedPhone(),
            "reason", "INVALID_CREDENTIALS"
        ),
        null,
        null
    ));

    Map<String, Object> body = new HashMap<>();
    body.put("error", "INVALID_CREDENTIALS");
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
}
```

#### 9. InternalCreateRefreshTokenService - SESSION_CREATE
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/InternalCreateRefreshTokenService.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор
- После сохранения session (строка ~78), добавить:

```java
var savedSession = sessionRepository.save(session);

// Audit: SESSION_CREATE
createAuditLogUseCase.create(new CreateAuditLogCommand(
    user.getId(),
    "SESSION_CREATE",
    user.getPhone(),
    command.ipAddress(),
    command.userAgent(),
    Map.of(
        "sessionId", savedSession.getId(),
        "expiresAt", savedSession.getExpiresAt().toString(),
        "deviceInfo", command.userAgent()
    ),
    "user_sessions",
    savedSession.getId()
));
```

#### 10. RevokeRefreshTokenService - SESSION_REVOKE + USER_LOGOUT
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RevokeRefreshTokenService.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор
- После отзыва session (строка ~55), добавить:

```java
session.setRevoked(true);
var updated = repository.save(session);

// Audit: SESSION_REVOKE
createAuditLogUseCase.create(new CreateAuditLogCommand(
    updated.getUser().getId(),
    "SESSION_REVOKE",
    updated.getUser().getPhone(),
    null, // IP недоступен в revoke flow
    null, // User-Agent недоступен
    Map.of(
        "sessionId", updated.getId(),
        "revokedAt", LocalDateTime.now().toString()
    ),
    "user_sessions",
    updated.getId()
));

// Audit: USER_LOGOUT
createAuditLogUseCase.create(new CreateAuditLogCommand(
    updated.getUser().getId(),
    "USER_LOGOUT",
    updated.getUser().getPhone(),
    null,
    null,
    Map.of("sessionId", updated.getId()),
    null,
    null
));
```

#### 11. RefreshAccessTokenService - TOKEN_REFRESH
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RefreshAccessTokenService.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор
- После обновления токена (строка ~88), добавить:

```java
// Audit: TOKEN_REFRESH
createAuditLogUseCase.create(new CreateAuditLogCommand(
    session.getUser().getId(),
    "TOKEN_REFRESH",
    session.getUser().getPhone(),
    command.ipAddress(),
    command.userAgent(),
    Map.of(
        "sessionId", session.getId(),
        "refreshedAt", LocalDateTime.now().toString()
    ),
    null,
    null
));
```

### Фаза 3: Операции управления пользователями

#### 12. Расширить Command объекты с IP/User-Agent

**UpdateUserPasswordCommand**:
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/port/in/command/UpdateUserPasswordCommand.java`

Добавить поля:
```java
String ipAddress,
String userAgent
```

**UpdateUserInfoCommand**:
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/port/in/command/UpdateUserInfoCommand.java`

Добавить поля:
```java
String ipAddress,
String userAgent
```

**UpdateUserPersonalInfoCommand**:
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/port/in/command/UpdateUserPersonalInfoCommand.java`

Добавить поля:
```java
String ipAddress,
String userAgent
```

#### 13. Обновить контроллеры для извлечения IP/User-Agent

**UserController**:
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/user/adapter/in/web/UserController.java`

Для эндпоинтов `updateUserInfo()`, `updatePassword()`, `updatePersonalInfo()`:
```java
@PutMapping("/info")
public ResponseEntity<UserDTO> updateUserInfo(
        @Valid @RequestBody UpdateUserInfoRequest request,
        HttpServletRequest httpRequest) {

    var command = new UpdateUserInfoCommand(
        getCurrentUserId(),
        request.email(),
        request.firstName(),
        request.lastName(),
        request.middleName(),
        httpRequest.getRemoteAddr(),
        httpRequest.getHeader("User-Agent")
    );
    // ...
}
```

#### 14. UpdateUserPasswordService - PASSWORD_CHANGE
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/usecase/UpdateUserPasswordService.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор
- После строки 38 (`userRepository.save(user);`), добавить:

```java
var saved = userRepository.save(user);

// Audit: PASSWORD_CHANGE
createAuditLogUseCase.create(new CreateAuditLogCommand(
    saved.getId(),
    "PASSWORD_CHANGE",
    saved.getPhone(),
    command.ipAddress(),
    command.userAgent(),
    Map.of("changedAt", LocalDateTime.now().toString()),
    "users",
    saved.getId()
));
```

#### 15. UpdateUserInfoService - USER_UPDATE
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/usecase/UpdateUserInfoService.java`

**Изменения**:
- Добавить `private final CreateAuditLogUseCase createAuditLogUseCase;` в конструктор
- После сохранения (строка ~53), добавить:

```java
var saved = userRepository.save(user);

// Audit: USER_UPDATE
List<String> updatedFields = new ArrayList<>();
Map<String, Object> newValues = new HashMap<>();
newValues.put("updatedFields", updatedFields);

if (command.email() != null) {
    updatedFields.add("email");
    newValues.put("email", saved.getEmail());
}
if (command.firstName() != null) {
    updatedFields.add("firstName");
    newValues.put("firstName", saved.getFirstName());
}
// ... аналогично для lastName, middleName

createAuditLogUseCase.create(new CreateAuditLogCommand(
    saved.getId(),
    "USER_UPDATE",
    saved.getPhone(),
    command.ipAddress(),
    command.userAgent(),
    newValues,
    "users",
    saved.getId()
));
```

#### 16. UpdateUserPersonalInfoService - USER_UPDATE
**Файл**: `backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/usecase/UpdateUserPersonalInfoService.java`

Аналогичные изменения как в UpdateUserInfoService.

### Фаза 4: Тестирование

#### Unit тесты

**CreateAuditLogServiceTest**:
**Файл**: `backend/user-auth-service/src/test/java/com/logistics/userauth/audit/application/usecase/CreateAuditLogServiceTest.java`

Тесты:
- `shouldCreateAuditLogWithValidCommand()` - проверить что repository.save() вызван корректно
- `shouldHandleNullUserId()` - для случая USER_LOGIN_FAILURE
- `shouldCacheActionTypesOnStartup()` - проверить кэширование в @PostConstruct
- `shouldNotThrowWhenAuditFails()` - проверить что обработка ошибок не ломает flow

**Обновить существующие тесты сервисов**:
- RegisterUserServiceTest: проверить что `createAuditLogUseCase.create()` вызван
- AuthenticateUserServiceTest: проверить аудит логирование при успехе
- RevokeRefreshTokenServiceTest: проверить что SESSION_REVOKE и USER_LOGOUT залогированы
- UpdateUserPasswordServiceTest: проверить что PASSWORD_CHANGE залогирован

#### Integration тесты

**AuditLoggingIntegrationTest**:
**Файл**: `backend/user-auth-service/src/test/java/com/logistics/userauth/audit/integration/AuditLoggingIntegrationTest.java`

Использовать аннотацию `@IntegrationTest` (Testcontainers с PostgreSQL + Redis).

Тесты для каждой операции:
1. `shouldLogUserRegister()` - регистрация пользователя, проверить что существуют логи USER_REGISTER + SESSION_CREATE
2. `shouldLogUserLoginSuccess()` - аутентификация, проверить лог USER_LOGIN_SUCCESS
3. `shouldLogUserLoginFailure()` - аутентификация с неверным паролем, проверить лог USER_LOGIN_FAILURE
4. `shouldLogUserLogout()` - отзыв токена, проверить логи SESSION_REVOKE + USER_LOGOUT
5. `shouldLogPasswordChange()` - смена пароля, проверить лог PASSWORD_CHANGE
6. `shouldLogTokenRefresh()` - обновление токена, проверить лог TOKEN_REFRESH
7. `shouldLogUserUpdate()` - обновление инфо пользователя, проверить лог USER_UPDATE

Проверять:
- Корректный actionTypeName
- Корректный userId (или null для failures)
- IP address захвачен
- User-Agent захвачен
- newValues JSONB имеет ожидаемую структуру
- Нет чувствительных данных (паролей) в newValues
- tableName и recordId установлены корректно

### Фаза 5: Верификация

#### Ручное тестирование

1. Запустить сервисы: `cd docker && docker-compose up -d`
2. Тестировать каждую операцию через Swagger UI: `http://localhost:8081/api/v1/swagger-ui.html`
3. Проверить таблицу audit_logs: `SELECT * FROM user_management.audit_logs ORDER BY performed_at DESC;`

#### Тестовые сценарии

1. **Регистрация пользователя**:
   - POST /api/v1/auth/register
   - Проверить: созданы логи USER_REGISTER + SESSION_CREATE
   - Проверить: newValues содержит email, phone, role, firstName, lastName (без пароля)

2. **Успешный вход**:
   - POST /api/v1/auth/signin
   - Проверить: создан лог USER_LOGIN_SUCCESS
   - Проверить: захвачены IP address и User-Agent

3. **Неудачный вход**:
   - POST /api/v1/auth/signin с неверным паролем
   - Проверить: создан лог USER_LOGIN_FAILURE
   - Проверить: userId is null, attemptedPhone захвачен

4. **Выход**:
   - POST /api/v1/auth/logout
   - Проверить: созданы логи SESSION_REVOKE + USER_LOGOUT

5. **Обновление токена**:
   - POST /api/v1/auth/refresh
   - Проверить: создан лог TOKEN_REFRESH

6. **Смена пароля**:
   - PUT /api/v1/user/password
   - Проверить: создан лог PASSWORD_CHANGE
   - Проверить: newValues НЕ содержит пароль

7. **Обновление инфо пользователя**:
   - PUT /api/v1/user/info
   - Проверить: создан лог USER_UPDATE
   - Проверить: newValues содержит только измененные поля

## Маппинг данных: Содержимое newValues JSONB

| Action Type | Содержимое newValues | Чувствительные данные? |
|------------|---------------------|----------------------|
| USER_REGISTER | `{"email": "...", "phone": "...", "role": "CLIENT", "firstName": "...", "lastName": "..."}` | Нет |
| USER_LOGIN_SUCCESS | `{"userId": 123}` | Нет |
| USER_LOGIN_FAILURE | `{"attemptedPhone": "+79001234567", "reason": "INVALID_CREDENTIALS"}` | Нет |
| USER_LOGOUT | `{"sessionId": 456}` | Нет |
| PASSWORD_CHANGE | `{"changedAt": "2025-01-27T10:30:00"}` | Нет |
| SESSION_CREATE | `{"sessionId": 456, "expiresAt": "...", "deviceInfo": "..."}` | Нет |
| SESSION_REVOKE | `{"sessionId": 456, "revokedAt": "..."}` | Нет |
| TOKEN_REFRESH | `{"sessionId": 456, "refreshedAt": "..."}` | Нет |
| USER_UPDATE | `{"updatedFields": ["email", "firstName"], "email": "new@example.com", "firstName": "John"}` | Нет |

**Примечание безопасности**: НИКОГДА не логировать пароли (plain или hashed), session токены или другие credentials в newValues.

## Критические файлы

**Новые файлы**:
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/port/in/CreateAuditLogUseCase.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/port/in/command/CreateAuditLogCommand.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/audit/application/usecase/CreateAuditLogService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/exception/AuthenticationFailedException.java`

**Модифицируемые файлы**:
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RegisterUserService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/AuthenticateUserService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/InternalCreateRefreshTokenService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RevokeRefreshTokenService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RefreshAccessTokenService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/usecase/UpdateUserPasswordService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/usecase/UpdateUserInfoService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/usecase/UpdateUserPersonalInfoService.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/common/web/GlobalExceptionHandler.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/user/adapter/in/web/UserController.java`

**Commands для расширения**:
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/port/in/command/UpdateUserPasswordCommand.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/port/in/command/UpdateUserInfoCommand.java`
- `/backend/user-auth-service/src/main/java/com/logistics/userauth/user/application/port/in/command/UpdateUserPersonalInfoCommand.java`

## Примечания

- Аудит логирование НИКОГДА не должно ломать бизнес операции - обернуть в try-catch если нужно
- Action types кэшируются при старте для производительности
- IP адреса хранятся как PostgreSQL INET тип
- newValues это JSONB для гибкой схемы
- Рассмотреть @Async для аудит логирования в будущем для лучшей производительности
- Добавить retention policy для старых audit logs в будущем

## Команды для тестирования

```bash
# Запуск всех тестов
cd backend/user-auth-service
mvn test

# Запуск только unit тестов
mvn test -Dtest=CreateAuditLogServiceTest

# Запуск integration тестов
mvn test -Dtest=AuditLoggingIntegrationTest

# Сборка проекта
cd backend
mvn clean install

# Запуск через Docker Compose
cd docker
docker-compose up -d

# Просмотр логов
docker-compose logs -f user-auth-service

# Проверка audit_logs в БД
docker exec -it logistics-postgres psql -U logistics_user -d logistics_db
SELECT * FROM user_management.audit_logs ORDER BY performed_at DESC LIMIT 10;
```