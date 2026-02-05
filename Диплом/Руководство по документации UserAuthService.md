
## 1. Зависимости для pom.xml

### Parent POM (backend/pom.xml)

Добавить в `<properties>` раздел:

```xml
# <!-- Документация и API -->
<springdoc.version>2.3.0</springdoc.version>
<swagger-annotations.version>2.2.18</swagger-annotations.version>
```

Добавить в `<dependencyManagement>`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-common</artifactId>
    <version>${springdoc.version}</version>
</dependency>

<dependency>
    <groupId>io.swagger.core.v3</groupId>
    <artifactId>swagger-annotations</artifactId>
    <version>${swagger-annotations.version}</version>
</dependency>
```

### Child POM (backend/user-auth-service/pom.xml)

Добавить в `<dependencies>`:

```xml
<!-- Swagger/OpenAPI документация -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>

<dependency>
    <groupId>io.swagger.core.v3</groupId>
    <artifactId>swagger-annotations</artifactId>
</dependency>
```

---

## 2. Документация по файлам

### A. AuthController.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/adapter/in/web/AuthController.java`

**Добавить на класс:**

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(
    name = "Аутентификация",
    description = "REST API endpoints для регистрации, логина, refresh токенов и выхода из системы"
)
public class AuthController {
    // ...
    
    @PostMapping("/sign-in")
    @Operation(
        summary = "Авторизация пользователя",
        description = "Проверяет учетные данные пользователя (телефон/пароль) и выдает JWT access token и refresh token",
        tags = {"Аутентификация"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Успешная авторизация",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Неверные учетные данные (телефон или пароль)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации входных данных"
        )
    })
    public ResponseEntity<JwtAuthenticationResponse> signIn(
        @Valid @RequestBody SignInRequest request,
        HttpServletRequest httpRequest
    ) {
        // ...
    }
    
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Регистрация нового пользователя",
        description = "Создает новый аккаунт пользователя с ролью CLIENT и выдает JWT tokens",
        tags = {"Аутентификация"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно зарегистрирован"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Пользователь с таким телефоном или email уже существует"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации входных данных"
        )
    })
    public ResponseEntity<JwtAuthenticationResponse> signUp(
        @Valid @RequestBody SignUpRequest request,
        HttpServletRequest httpRequest
    ) {
        // ...
    }
    
    @PostMapping("/refresh")
    @Operation(
        summary = "Обновление access токена",
        description = "Использует refresh token для выдачи нового access token и нового refresh token (token rotation)",
        tags = {"Аутентификация"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Новые токены успешно выданы"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token невалиден, истек или был отозван"
        )
    })
    public ResponseEntity<JwtAuthenticationResponse> refresh(
        @Valid @RequestBody RefreshTokenRequest request,
        HttpServletRequest httpRequest
    ) {
        // ...
    }
    
    @PostMapping("/logout")
    @Operation(
        summary = "Выход из системы",
        description = "Отзывает (revoke) refresh token, делая его неиспользуемым",
        tags = {"Аутентификация"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Успешный выход"),
        @ApiResponse(responseCode = "401", description = "Refresh token невалиден")
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        // ...
    }
}
```

---

### B. RefreshAccessTokenService.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RefreshAccessTokenService.java`

**Добавить JavaDoc:**

```java
/**
 * Сервис для обновления access токена с использованием refresh токена.
 * 
 * Реализует паттерн Token Rotation:
 * 1. Принимает старый refresh token
 * 2. Проверяет его валидность (не истек, не отозван)
 * 3. Помечает старый refresh token как отозванный
 * 4. Выдает новый access token и новый refresh token
 * 
 * Это повышает безопасность: если refresh token будет скомпрометирован,
 * злоумышленник сможет его использовать только один раз.
 */
@Service
@RequiredArgsConstructor
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {

    private final UserSessionRepository repository;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    /**
     * Обновляет access токен на основе refresh токена.
     * 
     * @param command Команда содержит:
     *                - refreshToken: старый refresh token
     *                - ipAddress: IP-адрес клиента
     *                - userAgent: браузер клиента
     * 
     * @return JwtAuthenticationResponse с новыми access и refresh токенами
     * 
     * @throws InvalidRefreshTokenException если:
     *         - Токен не найден в БД
     *         - Токен был отозван
     *         - Токен истек (expiresAt < now)
     */
    @Override
    @Transactional
    public JwtAuthenticationResponse refresh(RefreshAccessTokenCommand command) {
        // ...
    }

    /**
     * Валидирует refresh token перед использованием.
     * 
     * @param session Сессия пользователя
     * @throws InvalidRefreshTokenException если токен отозван или истек
     */
    private static void validate(UserSession session) {
        // ...
    }
}
```

---

### C. InternalCreateRefreshTokenService.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/InternalCreateRefreshTokenService.java`

**Добавить JavaDoc:**

```java
/**
 * Внутренний сервис для создания refresh токенов.
 * 
 * Отмечен как "Internal" (внутренний) потому что:
 * - Не должен вызваться напрямую из контроллеров
 * - Используется другими use cases (Register, Authenticate, Refresh)
 * - Инкапсулирует логику создания и сохранения сессии
 * 
 * Каждый refresh token уникален и привязан к:
 * - Конкретному пользователю
 * - Конкретному устройству (IP + User-Agent)
 * - Определенному времени истечения (TTL из конфигурации)
 */
@Service
@RequiredArgsConstructor
public class InternalCreateRefreshTokenService implements InternalCreateRefreshTokenUseCase {

    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenTtlSeconds;

    /**
     * Создает новый refresh token для пользователя.
     * 
     * @param command Команда содержит:
     *                - userId: ID пользователя
     *                - ipAddress: IP-адрес клиента
     *                - userAgent: User-Agent браузера
     * 
     * @return UUID строка, которая служит refresh токеном
     * 
     * @throws RuntimeException если пользователь с таким ID не найден
     * 
     * @see UserSession для деталей сохраненной сессии
     */
    @Override
    public String create(CreateRefreshTokenCommand command) {
        // ...
    }
}
```

---

### D. RevokeRefreshTokenService.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/usecase/RevokeRefreshTokenService.java`

**Добавить JavaDoc:**

```java
/**
 * Сервис для отзыва (revoke) refresh токенов.
 * 
 * Отзыв токена делает его непригодным для дальнейшего использования.
 * Используется при logout и других операциях завершения сессии.
 * 
 * При попытке использовать отозванный токен для обновления access token
 * будет выброшено исключение InvalidRefreshTokenException.
 */
@Service
@RequiredArgsConstructor
public class RevokeRefreshTokenService implements RevokeRefreshTokenUseCase {

    private final UserSessionRepository repository;

    /**
     * Отзывает refresh token, делая его неиспользуемым.
     * 
     * @param command Команда содержит refreshToken для отзыва
     * 
     * @throws InvalidRefreshTokenException если токен не найден в БД
     */
    @Override
    public void revoke(RevokeRefreshTokenCommand command) {
        // ...
    }
}
```

---

### E. UserSessionEntity.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/session/adapter/out/persistence/UserSessionEntity.java`

**Добавить JavaDoc:**

```java
/**
 * JPA Entity для хранения сессий пользователей и их refresh токенов.
 * 
 * Таблица: user_management.user_sessions
 * 
 * Основные поля:
 * - sessionId: Уникальный ID сессии (первичный ключ)
 * - userId: Внешний ключ на таблицу users
 * - refreshToken: UUID токен для обновления access token
 * - expiresAt: Время истечения refresh token (обычно 7-30 дней)
 * - createdAt: Время создания сессии
 * - revoked: Флаг отзыва (true = токен больше не валиден)
 * - ipAddress: IP-адрес клиента (для аудита)
 * - userAgent: User-Agent браузера (для аудита)
 * 
 * Индексы:
 * - idx_user_sessions_user_id: для быстрого поиска сессий пользователя
 * - idx_user_sessions_expires_at: для очистки истекших сессий
 * 
 * Уникальные ограничения:
 * - refresh_token должен быть уникален (один токен = одна сессия)
 * 
 * @see UserSession доменная модель
 * @see UserSessionPersistenceMapper преобразование Entity ↔ Domain
 */
@Data
@Entity
@Table(name = "user_sessions", schema = "user_management", ...)
public class UserSessionEntity {
    // ...
}
```

---

### F. UserSession.java (Domain)

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/session/domain/UserSession.java`

**Добавить JavaDoc:**

```java
/**
 * Доменная модель для сессии пользователя.
 * 
 * Представляет активную сессию пользователя с привязанным refresh токеном.
 * 
 * Жизненный цикл сессии:
 * 1. СОЗДАНИЕ: При регистрации или логине создается новая сессия с refresh токеном
 * 2. ИСПОЛЬЗОВАНИЕ: Клиент использует refresh токен для получения новых access токенов
 * 3. ROTATION: При каждом использовании старый refresh токен помечается как revoked
 * 4. ИСТЕЧЕНИЕ: По истечении TTL сессия удаляется из БД (очистка кроном)
 * 
 * Безопасность:
 * - Refresh token уникален для каждой сессии
 * - Привязан к IP-адресу и User-Agent для дополнительной защиты
 * - Может быть явно отозван пользователем (logout)
 * - Имеет ограниченное время жизни (TTL)
 * 
 * @see UserSessionEntity JPA представление в БД
 */
@Data
@Builder
public class UserSession {
    
    private long id;
    
    /** Пользователь, которому принадлежит сессия */
    private User user;
    
    /** UUID токен для обновления access token */
    private String refreshToken;
    
    /** Время создания сессии */
    private LocalDateTime createdAt;
    
    /** Время истечения сессии (обычно +7 дней от создания) */
    private LocalDateTime expiresAt;
    
    /** IP-адрес клиента для аудита и безопасности */
    private Inet ipAddress;
    
    /** User-Agent браузера для идентификации устройства */
    private String userAgent;
    
    /** Флаг отзыва: true = токен больше не может быть использован */
    private boolean revoked;
}
```

---

### G. InvalidRefreshTokenException.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/application/exception/InvalidRefreshTokenException.java`

**Добавить JavaDoc:**

```java
/**
 * Исключение выбрасываемое когда refresh token невалиден или не может быть использован.
 * 
 * Случаи когда выбрасывается:
 * 1. Token not found: Токен отсутствует в БД
 * 2. Token expired: Токен истек (current time > expiresAt)
 * 3. Token revoked: Токен был явно отозван (revoked = true)
 * 4. Token rotated: Токен был заменен на новый (pattern token rotation)
 * 
 * HTTP ответ: 401 Unauthorized
 * 
 * Обработка: @ExceptionHandler в GlobalExceptionHandler возвращает
 * {
 *   "error": "INVALID_REFRESH_TOKEN",
 *   "message": "..."
 * }
 * 
 * @see GlobalExceptionHandler для обработки этого исключения
 * @see RefreshAccessTokenService где выбрасывается
 */
public class InvalidRefreshTokenException extends RuntimeException {
    
    /**
     * Создает новое исключение с сообщением об ошибке.
     * 
     * @param message Человеко-читаемое описание причины (будет возвращено клиенту)
     */
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
```

---

### H. GlobalExceptionHandler.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/common/web/GlobalExceptionHandler.java`

**Добавить JavaDoc на класс:**

```java
/**
 * Глобальный обработчик исключений для всех REST endpoints.
 * 
 * Обеспечивает единообразный формат ошибок для всех API endpoints:
 * {
 *   "error": "ERROR_CODE",
 *   "message": "Human-readable message",
 *   "fields": { "fieldName": "error message" }  // только для VALIDATION_FAILED
 * }
 * 
 * Обработка исключений:
 * - BadCredentialsException → 401 INVALID_CREDENTIALS
 * - DataIntegrityViolationException → 409 CONFLICT
 * - MethodArgumentNotValidException → 400 VALIDATION_FAILED
 * - InvalidRefreshTokenException → 401 INVALID_REFRESH_TOKEN
 * - Все остальные Exception → 500 INTERNAL_SERVER_ERROR
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок аутентификации (неверные учетные данные).
     * 
     * @param ex BadCredentialsException
     * @return ResponseEntity с кодом 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        // ...
    }

    /**
     * Обработка ошибок целостности данных (duplicate keys, constraint violations).
     * 
     * @param ex DataIntegrityViolationException
     * @return ResponseEntity с кодом 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        // ...
    }

    /**
     * Обработка ошибок валидации входных параметров.
     * 
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity с кодом 400 и деталями ошибок по полям
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // ...
    }

    /**
     * Обработка ошибок невалидного refresh token.
     * 
     * @param ex InvalidRefreshTokenException
     * @return ResponseEntity с кодом 401
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        // ...
    }
}
```

---

### I. SignUpRequest.java & SignInRequest.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/user/adapter/in/web/dto/`

**Добавить JavaDoc:**

```java
/**
 * Запрос для регистрации нового пользователя.
 * 
 * Валидация:
 * - email: Должен быть в формате email
 * - phone: Формат +7XXXXXXXXXX (Россия), +375XXXXXXXXX (Беларусь), +77XXXXXXXXX (Казахстан)
 * - password: Минимум 8 символов, включая цифру, заглавную и строчную букву и спецсимвол
 * - firstName, lastName, middleName: Не пусты
 * 
 * Пример:
 * {
 *   "email": "john@example.com",
 *   "phone": "+79991234567",
 *   "password": "Password123!",
 *   "firstName": "Иван",
 *   "lastName": "Иванов",
 *   "middleName": "Иванович"
 * }
 */
public record SignUpRequest(
    @Email
    String email,
    
    @NotNull
    @Phone
    String phone,
    
    @Password
    String password,
    
    @NotBlank
    String firstName,
    
    @NotBlank
    String lastName,
    
    @NotBlank
    String middleName
) {}
```

```java
/**
 * Запрос для авторизации пользователя.
 * 
 * Валидация:
 * - phone: Обязателен, формат +7/375/77...
 * - password: Обязателен, сложный пароль
 * - email: Опциональный альтернативный способ входа
 * 
 * Пример:
 * {
 *   "phone": "+79991234567",
 *   "password": "Password123!"
 * }
 */
public record SignInRequest(
    @NotNull
    @Phone
    String phone,
    
    @Email
    String email,
    
    @Password
    String password
) {}
```

---

### J. RefreshTokenRequest.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/adapter/in/web/dto/RefreshTokenRequest.java`

**Добавить JavaDoc:**

```java
/**
 * Запрос для обновления access token или выхода из системы.
 * 
 * Используется для:
 * 1. POST /auth/refresh - получить новый access token
 * 2. POST /auth/logout - отозвать (invalidate) текущую сессию
 * 
 * Пример:
 * {
 *   "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
 * }
 */
public record RefreshTokenRequest(String refreshToken) {}
```

---

### K. JwtAuthenticationResponse.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/adapter/in/web/dto/JwtAuthenticationResponse.java`

**Добавить JavaDoc:**

```java
/**
 * Ответ с JWT токенами после успешной аутентификации или регистрации.
 * 
 * Содержит:
 * - accessToken: JWT токен для доступа к защищенным ресурсам (TTL: 15-60 минут)
 * - refreshToken: Токен для получения нового accessToken без повторной аутентификации (TTL: 7-30 дней)
 * 
 * Пример:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjk3NTAwMzIzfQ.xxxx",
 *   "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
 * }
 * 
 * Использование:
 * 1. accessToken используется в header: Authorization: Bearer {accessToken}
 * 2. refreshToken хранится безопасно на клиенте (при истечении accessToken)
 */
@Builder
public record JwtAuthenticationResponse(
    String accessToken,
    String refreshToken
) {}
```

---

### L. JwtTokenProvider.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/adapter/out/JwtTokenProvider.java`

**Добавить JavaDoc:**

```java
/**
 * Провайдер для генерации и валидации JWT access токенов.
 * 
 * Использует HS256 (HMAC SHA256) алгоритм подписания.
 * 
 * Структура JWT:
 * - Header: { "alg": "HS256" }
 * - Payload: { "sub": "userId", "iat": timestamp, "exp": timestamp, "phone": "...", "role": "..." }
 * - Signature: HMAC(secret, header.payload)
 * 
 * Конфигурация в application.yml:
 * app:
 *   jwt:
 *     secret: "your-secret-key-change-in-production"
 *     expiration: 3600  # 1 час в секундах
 * 
 * @see JwtAuthenticationFilter для проверки токена в каждом запросе
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenGeneratorPort {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long accessTokenTtlSeconds;

    /**
     * Генерирует новый JWT access token для пользователя.
     * 
     * @param user Пользователь, для которого создается токен
     * @return Подписанный JWT токен в виде строки
     */
    @Override
    public String generateAccessToken(User user) {
        // ...
    }

    /**
     * Проверяет валидность JWT токена.
     * 
     * @param token JWT токен для проверки
     * @return true если токен валиден и не истек
     */
    @Override
    public boolean isTokenValid(String token) {
        // ...
    }

    /**
     * Извлекает ID пользователя из JWT токена.
     * 
     * @param token JWT токен
     * @return ID пользователя (значение "sub" claim)
     */
    @Override
    public Long extractUserId(String token) {
        // ...
    }
}
```

---

### M. JwtAuthenticationFilter.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/adapter/in/security/JwtAuthenticationFilter.java`

**Добавить JavaDoc:**

```java
/**
 * Фильтр для аутентификации запросов на основе JWT токенов.
 * 
 * Применяется ко ВСЕМ запросам кроме /auth/** endpoints.
 * 
 * Процесс:
 * 1. Читает header Authorization
 * 2. Извлекает Bearer токен
 * 3. Валидирует токен через JwtTokenProvider
 * 4. Извлекает userId из токена
 * 5. Загружает пользователя из БД
 * 6. Создает Authentication объект и устанавливает в SecurityContext
 * 7. Передает запрос дальше по цепочке
 * 
 * Если токен невалиден:
 * - Запрос передается дальше БЕЗ аутентификации
 * - Spring Security вернет 403 Forbidden для защищенных ресурсов
 * 
 * Интеграция:
 * @see SecurityConfiguration где регистрируется этот фильтр
 * @see JwtTokenProvider для валидации токенов
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Выполняет фильтрацию и аутентификацию.
     * 
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param filterChain Цепочка фильтров
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // ...
    }

    /**
     * Исключает /auth/** endpoints из обработки этого фильтра.
     * 
     * @param request HTTP запрос
     * @return true если запрос НЕ должен быть обработан этим фильтром
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // ...
    }
}
```

---

### N. SecurityConfiguration.java

**Где:** `backend/user-auth-service/src/main/java/com/logistics/userauth/auth/jwt/infrastructure/security/SecurityConfiguration.java`

**Добавить JavaDoc:**

```java
/**
 * Конфигурация Spring Security для JWT-based аутентификации.
 * 
 * Особенности:
 * - Stateless сессии (не используются cookies и sessions)
 * - CSRF отключен (для REST API с JWT не требуется)
 * - JWT фильтр регистрируется в цепочке безопасности
 * - /auth/** endpoints открыты для всех
 * - Остальные endpoints требуют валидный JWT токен
 * 
 * Архитектура:
 * 1. JwtAuthenticationFilter → извлекает и валидирует токен
 * 2. AuthenticationProvider → аутентифицирует user/password при логине
 * 3. SecurityFilterChain → определяет какие endpoint защищены
 * 
 * @see JwtAuthenticationFilter для деталей обработки JWT
 * @see LogisticsUserDetailsService для загрузки пользователя из БД
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    
    /**
     * Определяет цепочку фильтров безопасности.
     * 
     * @param http HttpSecurity для конфигурации
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // ...
    }
    
    /**
     * Провайдер аутентификации для username/password при логине.
     * 
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // ...
    }
}
```

---

### O. Tests (GlobalExceptionHandlerTest.java, AuthControllerIntegrationTest.java)

**Для всех тестов добавить JavaDoc на класс:**

```java
/**
 * Тесты обработчика исключений.
 * 
 * Проверяет:
 * ✓ Корректный HTTP статус код
 * ✓ Правильный формат JSON ответа
 * ✓ Корректное сообщение об ошибке
 * ✓ Все типы исключений обрабатываются
 */
@DisplayName("GlobalExceptionHandler: юнит-тесты")
class GlobalExceptionHandlerTest {
    // ...
}
```

```java
/**
 * Интеграционные тесты REST API аутентификации.
 * 
 * Тестирует полные сценарии:
 * ✓ Регистрация нового пользователя
 * ✓ Логин и получение токенов
 * ✓ Обновление access token через refresh token
 * ✓ Logout и отзыв refresh token
 * ✓ Обработка невалидных токенов
 * ✓ Обработка ошибок валидации
 * 
 * Использует:
 * @IntegrationTest с testcontainers для реальной БД
 * @AutoConfigureMockMvc для тестирования HTTP endpoints
 * ObjectMapper для сериализации/десериализации JSON
 */
@IntegrationTest
@AutoConfigureMockMvc
@DisplayName("AuthController: интеграционные тесты")
class AuthControllerIntegrationTest {
    // ...
}
```

---

## 3. Конфигурация OpenAPI

**Создать файл:** `backend/user-auth-service/src/main/java/com/logistics/userauth/config/OpenApiConfig.java`

```java
package com.logistics.userauth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI (Swagger) документации.
 * 
 * Доступна по адресу: http://localhost:8080/swagger-ui.html
 * JSON схема: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Auth Service API")
                        .version("1.0.0")
                        .description("REST API для аутентификации и управления пользователями в системе логистики")
                        .contact(new Contact()
                                .name("Logistics Team")
                                .email("support@logistics.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT access token для защищенных endpoint'ов")));
    }
}
```

---

## 4. Импорты для JavaDoc

Добавить в класс импорты (если используются аннотации):

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
```

---

## 5. Свойства для конфигурации (application.yml)

```yaml
# JWT конфигурация
app:
  jwt:
    secret: "${JWT_SECRET:dev-secret-key-change-in-production}"
    expiration: 3600  # access token TTL в секундах (1 час)
    refresh-expiration: 604800  # refresh token TTL в секундах (7 дней)

# Swagger/OpenAPI конфигурация
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
  api-docs:
    path: /v3/api-docs
  show-actuator: false  # Скрыть actuator endpoints из документации
```

---

## 6. Резюме всех изменений

| Файл | Изменение | Тип |
|------|-----------|-----|
| `pom.xml` (parent) | Добавить springdoc зависимости | Конфиг |
| `pom.xml` (child) | Добавить springdoc зависимости | Конфиг |
| `AuthController.java` | @Tag, @Operation, @ApiResponse | JavaDoc + Swagger |
| `RefreshAccessTokenService.java` | JavaDoc с описанием логики | JavaDoc |
| `InternalCreateRefreshTokenService.java` | JavaDoc | JavaDoc |
| `RevokeRefreshTokenService.java` | JavaDoc | JavaDoc |
| `UserSessionEntity.java` | JavaDoc | JavaDoc |
| `UserSession.java` | JavaDoc каждого поля | JavaDoc |
| `InvalidRefreshTokenException.java` | JavaDoc | JavaDoc |
| `GlobalExceptionHandler.java` | JavaDoc на класс и методы | JavaDoc |
| `SignUpRequest.java` | JavaDoc | JavaDoc |
| `SignInRequest.java` | JavaDoc | JavaDoc |
| `RefreshTokenRequest.java` | JavaDoc | JavaDoc |
| `JwtAuthenticationResponse.java` | JavaDoc | JavaDoc |
| `JwtTokenProvider.java` | JavaDoc на класс и методы | JavaDoc |
| `JwtAuthenticationFilter.java` | JavaDoc на класс и методы | JavaDoc |
| `SecurityConfiguration.java` | JavaDoc на класс и методы | JavaDoc |
| `GlobalExceptionHandlerTest.java` | JavaDoc на класс | JavaDoc |
| `AuthControllerIntegrationTest.java` | JavaDoc на класс | JavaDoc |
| `OpenApiConfig.java` (новый файл) | Создать конфигурацию | Новый файл |
| `application.yml` | Добавить springdoc свойства | Конфиг |

---

## 7. Проверка после внедрения

После добавления всей документации:

1. ✅ Запустить приложение
2. ✅ Открыть http://localhost:8080/swagger-ui.html
3. ✅ Проверить что все endpoints видны
4. ✅ Попробовать выполнить тестовый запрос через Swagger UI
5. ✅ Проверить что описания корректные
6. ✅ Запустить тесты: `mvn clean test`

---

## 8. Дополнительные рекомендации

### Для улучшения в будущем:

1. **Добавить примеры запросов/ответов в @ApiResponse**
2. **Создать Postman коллекцию** для API
3. **Добавить асинхронный refresh** token rotation
4. **Добавить logging** в каждый service
5. **Добавить metrics** через Micrometer
**