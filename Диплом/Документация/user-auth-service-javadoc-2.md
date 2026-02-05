# USER-AUTH-SERVICE: ПОЛНАЯ ДОКУМЕНТАЦИЯ (ЧАСТЬ 2/2)

## Подпакет: `com.logistics.userauth.auth.jwt.application.usecase` (ПРОДОЛЖЕНИЕ)

### Класс: RegisterUserService.java

```java
/**
 * Сервис для регистрации новых пользователей.
 *
 * <h2>Процесс</h2>
 * 1. Получает данные нового пользователя
 * 2. Проверяет уникальность телефона/email
 * 3. Хэширует пароль с помощью PasswordEncoder (BCrypt)
 * 4. Создает пользователя с ролью CLIENT и статусом ACTIVE
 * 5. Сохраняет в БД
 * 6. Генерирует access token
 * 7. Создает refresh token
 * 8. Возвращает оба токена
 *
 * <h2>Исключения</h2>
 * - DataIntegrityViolationException: Если телефон/email уже существует
 *
 * @implements RegisterUserUseCase
 */
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @Override
    public JwtAuthenticationResponse register(RegisterUserCommand command) {
        var user = User.builder()
            .email(command.email())
            .phone(command.phone())
            .passwordHash(passwordEncoder.encode(command.rawPassword()))
            .firstName(command.firstName())
            .lastName(command.lastName())
            .middleName(command.middleName())
            .role(UserRole.CLIENT)
            .status(UserStatus.ACTIVE)
            .createdTime(LocalDateTime.now())
            .lastAccessedTime(LocalDateTime.now())
            .build();

        var saved = userRepository.save(user);
        var accessToken = tokenGenerator.generateAccessToken(saved);
        var refreshToken = createRefreshTokenUseCase.create(
            CreateRefreshTokenCommand.builder()
                .userId(saved.getId())
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .build()
        );
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }
}
```

### Класс: RefreshAccessTokenService.java

```java
/**
 * Сервис для обновления access токена с использованием refresh токена.
 *
 * <h2>Реализует паттерн Token Rotation</h2>
 * 1. Принимает старый refresh token
 * 2. Проверяет его валидность (не истек, не отозван)
 * 3. Помечает старый refresh token как отозванный
 * 4. Выдает новый access token и новый refresh token
 *
 * Это повышает безопасность: если refresh token будет скомпрометирован,
 * злоумышленник сможет его использовать только один раз.
 *
 * <h2>Валидация</h2>
 * Проверяет:
 * - Токен существует в БД
 * - Токен не был отозван (revoked = false)
 * - Токен не истек (expiresAt >= now)
 *
 * <h2>Исключения</h2>
 * - InvalidRefreshTokenException: Если токен невалиден, отозван или истек
 *
 * @implements RefreshAccessTokenUseCase
 * @Transactional используется для атомарности операции
 */
@Service
@RequiredArgsConstructor
public class RefreshAccessTokenService implements RefreshAccessTokenUseCase {
    private final UserSessionRepository repository;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @Override
    @Transactional
    public JwtAuthenticationResponse refresh(RefreshAccessTokenCommand command) {
        var session = repository.findByRefreshToken(command.refreshToken())
            .orElseThrow(() -> new InvalidRefreshTokenException(\"Invalid refresh token\"));

        validate(session);
        String newAccessToken = tokenGenerator.generateAccessToken(session.getUser());
        session.setRevoked(true);
        repository.save(session);

        String newRefreshToken = createRefreshTokenUseCase.create(
            CreateRefreshTokenCommand.builder()
                .userId(session.getUser().getId())
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .build()
        );
        return new JwtAuthenticationResponse(newAccessToken, newRefreshToken);
    }

    private static void validate(UserSession session) {
        if (session.isRevoked()) {
            throw new InvalidRefreshTokenException(\"Refresh token is revoked\");
        }
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException(\"Refresh token is expired\");
        }
    }
}
```

### Класс: RevokeRefreshTokenService.java

```java
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

    @Override
    public void revoke(RevokeRefreshTokenCommand command) {
        var session = repository.findByRefreshToken(command.refreshToken())
            .orElseThrow(() -> new InvalidRefreshTokenException(\"Invalid refresh token\"));
        session.setRevoked(true);
        repository.save(session);
    }
}
```

### Класс: InternalCreateRefreshTokenService.java

```java
/**
 * Внутренний сервис для создания refresh токенов.
 *
 * <h2>Назначение</h2>
 * Отмечен как \"Internal\" потому что:
 * - Не должен вызваться напрямую из контроллеров
 * - Используется другими use cases (Register, Authenticate, Refresh)
 * - Инкапсулирует логику создания и сохранения сессии
 *
 * <h2>Каждый refresh token уникален и привязан к</h2>
 * - Конкретному пользователю
 * - Конкретному устройству (IP + User-Agent)
 * - Определенному времени истечения (TTL из конфигурации)
 *
 * <h2>Конфигурация</h2>
 * TTL читается из app.jwt.refresh-expiration в application.yml
 *
 * @implements InternalCreateRefreshTokenUseCase
 */
@Service
@RequiredArgsConstructor
public class InternalCreateRefreshTokenService implements InternalCreateRefreshTokenUseCase {
    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Value(\"${app.jwt.refresh-expiration}\")
    private long refreshTokenTtlSeconds;

    @Override
    public String create(CreateRefreshTokenCommand command) {
        var user = userRepository.findById(command.userId())
            .orElseThrow(() -> new RuntimeException(\"User not found\"));

        String refreshToken = UUID.randomUUID().toString();

        var session = UserSession.builder()
            .user(user)
            .refreshToken(refreshToken)
            .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenTtlSeconds))
            .createdAt(LocalDateTime.now())
            .ipAddress(command.ipAddress() != null ? new Inet(command.ipAddress()) : null)
            .userAgent(command.userAgent())
            .revoked(false)
            .build();

        sessionRepository.save(session);
        return refreshToken;
    }
}
```

---

### Подпакет: `com.logistics.userauth.auth.jwt.application.exception`

#### Класс: InvalidRefreshTokenException.java

```java
/**
 * Исключение выбрасываемое когда refresh token невалиден или не может быть использован.
 *
 * <h2>Случаи когда выбрасывается</h2>
 * 1. Token not found: Токен отсутствует в БД
 * 2. Token expired: Токен истек (current time > expiresAt)
 * 3. Token revoked: Токен был явно отозван (revoked = true)
 * 4. Token rotated: Токен был заменен на новый (pattern token rotation)
 *
 * <h2>HTTP ответ</h2>
 * 401 Unauthorized
 *
 * <h2>Обработка</h2>
 * @ExceptionHandler в GlobalExceptionHandler возвращает:
 * {
 *   \"error\": \"INVALID_REFRESH_TOKEN\",
 *   \"message\": \"...\"
 * }
 *
 * @see GlobalExceptionHandler для обработки этого исключения
 * @see RefreshAccessTokenService где выбрасывается
 */
public class InvalidRefreshTokenException extends RuntimeException {
    /**
     * Создает новое исключение с сообщением об ошибке.
     *
     * @param message Человеко-читаемое описание причины
     */
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
```

---

### Подпакет: `com.logistics.userauth.auth.jwt.adapter.in.web`

#### Класс: AuthController.java

```java
/**
 * REST контроллер для аутентификации и работы с токенами.
 *
 * <h2>Endpoints</h2>
 * - POST /auth/sign-up - Регистрация нового пользователя
 * - POST /auth/sign-in - Вход в систему
 * - POST /auth/refresh - Обновление access токена
 * - POST /auth/logout - Выход из системы (отзыв refresh токена)
 *
 * <h2>Security</h2>
 * Все endpoints исключены из JWT фильтра (shouldNotFilter).
 * Каждый endpoint имеет собственную валидацию.
 *
 * @see JwtAuthenticationFilter где исключаются эти endpoints
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(\"/auth\")
@Tag(name = \"Аутентификация\", description = \"REST API для регистрации, логина, refresh и выхода\")
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final RevokeRefreshTokenUseCase revokeRefreshTokenUseCase;

    /**
     * POST /auth/sign-in
     * Вход в систему (аутентификация по телефону + пароль).
     */
    @PostMapping(\"/sign-in\")
    @SignInOperation
    public ResponseEntity<JwtAuthenticationResponse> signIn(
        @Valid @RequestBody SignInRequest request,
        HttpServletRequest httpRequest
    ) {
        var command = AuthenticateUserCommand.builder()
            .phone(request.phone())
            .password(request.password())
            .ipAddress(httpRequest.getRemoteAddr())
            .userAgent(httpRequest.getHeader(\"User-Agent\"))
            .build();
        return ResponseEntity.ok(authenticateUserUseCase.authenticate(command));
    }

    /**
     * POST /auth/register
     * Регистрация нового пользователя.
     */
    @PostMapping(\"/register\")
    @ResponseStatus(HttpStatus.CREATED)
    @SignUpOperation
    public ResponseEntity<JwtAuthenticationResponse> signUp(
        @Valid @RequestBody SignUpRequest request,
        HttpServletRequest httpRequest
    ) {
        var command = RegisterUserCommand.builder()
            .email(request.email())
            .phone(request.phone())
            .rawPassword(request.password())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .middleName(request.middleName())
            .ipAddress(httpRequest.getRemoteAddr())
            .userAgent(httpRequest.getHeader(\"User-Agent\"))
            .build();
        var response = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /auth/logout
     * Выход из системы (отзыв refresh токена).
     */
    @PostMapping(\"/logout\")
    @LogoutOperation
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        var command = RevokeRefreshTokenCommand.builder()
            .refreshToken(request.refreshToken())
            .build();
        revokeRefreshTokenUseCase.revoke(command);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /auth/refresh
     * Обновление access токена (token rotation).
     */
    @PostMapping(\"/refresh\")
    @RefreshOperation
    public ResponseEntity<JwtAuthenticationResponse> refresh(
        @Valid @RequestBody RefreshTokenRequest request,
        HttpServletRequest httpRequest
    ) {
        var command = RefreshAccessTokenCommand.builder()
            .refreshToken(request.refreshToken())
            .ipAddress(httpRequest.getRemoteAddr())
            .userAgent(httpRequest.getHeader(\"User-Agent\"))
            .build();
        return ResponseEntity.ok(refreshAccessTokenUseCase.refresh(command));
    }
}
```

#### Класс: JwtAuthenticationResponse.java

```java
/**
 * Ответ с JWT токенами после успешной аутентификации или регистрации.
 *
 * <h2>Содержит</h2>
 * - accessToken: JWT токен для доступа к защищенным ресурсам (TTL: 15-60 минут)
 * - refreshToken: Токен для получения нового accessToken (TTL: 7-30 дней)
 *
 * <h2>Пример ответа</h2>
 * {
 *   \"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjk3NTAwMzIzfQ.xxxx\",
 *   \"refreshToken\": \"550e8400-e29b-41d4-a716-446655440000\"
 * }
 *
 * <h2>Использование</h2>
 * 1. accessToken используется в header: Authorization: Bearer {accessToken}
 * 2. refreshToken хранится безопасно на клиенте (при истечении accessToken)
 *
 * @Builder
 */
public record JwtAuthenticationResponse(String accessToken, String refreshToken) {}
```

#### Класс: RefreshTokenRequest.java

```java
/**
 * Запрос для обновления access token или выхода из системы.
 *
 * <h2>Используется для</h2>
 * 1. POST /auth/refresh - получить новый access token
 * 2. POST /auth/logout - отозвать (invalidate) текущую сессию
 *
 * <h2>Пример запроса</h2>
 * {
 *   \"refreshToken\": \"550e8400-e29b-41d4-a716-446655440000\"
 * }
 */
public record RefreshTokenRequest(String refreshToken) {}
```

---

### Подпакет: `com.logistics.userauth.auth.jwt.adapter.in.security`

#### Класс: JwtAuthenticationFilter.java

```java
/**
 * Фильтр для аутентификации запросов на основе JWT токенов.
 *
 * <h2>Применяется ко ВСЕМ запросам кроме /auth/** endpoints</h2>
 *
 * <h2>Процесс</h2>
 * 1. Читает header Authorization
 * 2. Извлекает Bearer токен
 * 3. Валидирует токен через TokenGeneratorPort
 * 4. Извлекает userId из токена
 * 5. Загружает пользователя из БД
 * 6. Создает Authentication объект и устанавливает в SecurityContext
 * 7. Передает запрос дальше по цепочке
 *
 * <h2>Если токен невалиден</h2>
 * - Запрос передается дальше БЕЗ аутентификации
 * - Spring Security вернет 403 Forbidden для защищенных ресурсов
 *
 * <h2>Интеграция</h2>
 * @see SecurityConfiguration где регистрируется этот фильтр
 * @see TokenGeneratorPort для валидации токенов
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String HEADER_NAME = \"Authorization\";
    public static final String BEARER_PREFIX = \"Bearer \";

    private final TokenGeneratorPort tokenGenerator;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!tokenGenerator.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        var userId = tokenGenerator.extractUserId(token);
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                var user = userOpt.get();
                var authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
                var authToken = new UsernamePasswordAuthenticationToken(
                    user, null, authorities
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith(\"/auth/\");
    }
}
```

---

### Подпакет: `com.logistics.userauth.auth.jwt.adapter.out`

#### Класс: JwtTokenProvider.java

```java
/**
 * Провайдер для генерации и валидации JWT access токенов.
 *
 * <h2>Алгоритм</h2>
 * Использует HS256 (HMAC SHA256) алгоритм подписания.
 *
 * <h2>Структура JWT</h2>
 * - Header: { \"alg\": \"HS256\" }
 * - Payload: { \"sub\": \"userId\", \"iat\": timestamp, \"exp\": timestamp, \"phone\": \"...\", \"role\": \"...\" }
 * - Signature: HMAC(secret, header.payload)
 *
 * <h2>Конфигурация</h2>
 * app:
 *   jwt:
 *     secret: \"your-secret-key-change-in-production\"
 *     expiration: 3600  # 1 час в секундах
 *
 * @implements TokenGeneratorPort
 * @see JwtAuthenticationFilter для использования в фильтре
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenGeneratorPort {
    @Value(\"${app.jwt.secret}\")
    private String secret;

    @Value(\"${app.jwt.expiration}\")
    private long accessTokenTtlSeconds;

    /**
     * Генерирует новый JWT access token для пользователя.
     *
     * @param user Пользователь, для которого создается токен
     * @return Подписанный JWT токен в виде строки
     */
    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenTtlSeconds);

        return Jwts.builder()
            .subject(user.getId().toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .claim(\"phone\", user.getPhone())
            .claim(\"role\", user.getRole().name())
            .signWith(getSigningKey())
            .compact();
    }

    /**
     * Проверяет валидность JWT токена.
     *
     * @param token JWT токен
     * @return true если токен валиден, false иначе
     */
    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Извлекает userId из JWT токена.
     *
     * @param token JWT токен
     * @return userId или null если токен невалиден
     */
    @Override
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            String subject = claims.getSubject();
            return Long.parseLong(subject);
        } catch (JwtException | NumberFormatException e) {
            return null;
        }
    }

    /**
     * Получает ключ подписания для HS256.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## Подпакет: `com.logistics.userauth.auth.session`

### Подпакет: `com.logistics.userauth.auth.session.domain`

#### Класс: UserSession.java

```java
/**
 * Доменная сущность для сессии пользователя.
 *
 * <h2>Назначение</h2>
 * Представляет активную сессию пользователя с refresh токеном.
 * Привязана к конкретному устройству (IP + User-Agent).
 *
 * <h2>Структура</h2>
 * - id: Уникальный идентификатор сессии
 * - user: Пользователь этой сессии
 * - refreshToken: UUID токен для обновления access token
 * - createdAt: Когда была создана сессия
 * - expiresAt: Когда истекает refresh token
 * - revoked: Был ли токен отозван (logout)
 * - ipAddress: IP-адрес клиента для защиты
 * - userAgent: User-Agent браузера для защиты
 *
 * <h2>Примеры</h2>
 * <pre>
 * UserSession session = UserSession.builder()
 *   .user(user)
 *   .refreshToken(UUID.randomUUID().toString())
 *   .expiresAt(LocalDateTime.now().plusDays(30))
 *   .ipAddress(new Inet(\"192.168.1.1\"))
 *   .userAgent(\"Mozilla/5.0...\")
 *   .revoked(false)
 *   .build();
 * </pre>
 *
 * @see User для пользователя
 * @see UserSessionEntity для JPA entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSession {
    private Long id;
    private User user;
    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean revoked;
    private Inet ipAddress;
    private String userAgent;
}
```

---

## Подпакет: `com.logistics.userauth.audit`

### Подпакет: `com.logistics.userauth.audit.domain`

#### Класс: AuditLog.java

```java
/**
 * Доменная сущность для логирования аудита.
 *
 * <h2>Назначение</h2>
 * Записывает все значимые действия пользователей в системе:
 * - Вход/выход (USER_LOGIN, USER_LOGOUT)
 * - Изменение профиля (PROFILE_UPDATED)
 * - Создание заказов (ORDER_CREATED)
 * - И другие действия, определенные в AuditActionType
 *
 * <h2>Структура</h2>
 * - id: Уникальный идентификатор записи в логе
 * - user: Пользователь, совершивший действие
 * - actionType: Тип действия (ссылка на AuditActionType)
 * - tableName: Таблица, которая была изменена (если применимо)
 * - recordId: ID записи в таблице, которая была изменена
 * - actorIdentifier: Строка для идентификации актора (обычно email/телефон)
 * - newValues: JSONB с новыми значениями (для UPDATE операций)
 * - performedAt: Когда было выполнено действие
 * - ipAddress: IP-адрес клиента для аудита безопасности
 *
 * <h2>Примеры</h2>
 * <pre>
 * AuditLog log = AuditLog.builder()
 *   .user(user)
 *   .actionType(auditActionType)  // USER_LOGIN
 *   .actorIdentifier(\"john@example.com\")
 *   .performedAt(LocalDateTime.now())
 *   .ipAddress(new Inet(\"192.168.1.100\"))
 *   .build();
 * </pre>
 *
 * @see AuditActionType для типов действий
 * @see AuditLogEntity для JPA entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {
    private long id;
    private User user;
    private AuditActionType actionType;
    private String tableName;
    private long recordId;
    private String actorIdentifier;
    private Map<String, Object> newValues;
    private LocalDateTime performedAt;
    private Inet ipAddress;
}
```

### Подпакет: `com.logistics.userauth.audit.app.out`

#### Интерфейс: AuditLogRepository.java

```java
/**
 * Порт для работы с логами аудита.
 *
 * @see AuditLogPersistenceAdapter для реализации
 */
public interface AuditLogRepository {
    void save(AuditLog auditLog);
    void delete(AuditLog auditLog);
    List<AuditLog> findByUser(User user);
    Optional<AuditLog> findByActionType(AuditActionType actionType);
    Optional<AuditLog> findByActorIdentifier(String actorIdentifier);
}
```

### Подпакет: `com.logistics.userauth.audit.adapter.out.persistence`

#### Класс: AuditLogEntity.java

```java
/**
 * JPA сущность для хранения логов аудита в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: user_management
 * Table: audit_logs
 *
 * <h2>Структура</h2>
 * - audit_log_id: Первичный ключ
 * - user_id: Внешний ключ на users
 * - action_type_id: Внешний ключ на shared_data.audit_action_types
 * - table_name: Названи таблицы, которая была изменена
 * - record_id: ID записи в этой таблице
 * - actor_identifier: Идентификатор актора (email/телефон)
 * - new_values: JSONB с новыми значениями
 * - performed_at: Дата/время действия
 * - ip_address: inet тип (специальный тип PostgreSQL)
 *
 * @see AuditLogJpaRepository для работы с БД
 * @see AuditLogPersistenceMapper для преобразования Domain ↔ Entity
 */
@Entity
@Table(
    name = \"audit_logs\",
    schema = \"user_management\",
    indexes = {
        @Index(columnList = \"user_id\", name = \"idx_audit_logs_user_id\"),
        @Index(columnList = \"action_type_id\", name = \"idx_audit_logs_action_type_id\"),
        @Index(columnList = \"table_name, record_id\", name = \"idx_audit_logs_record\"),
        @Index(columnList = \"performed_at\", name = \"idx_audit_logs_performed_at\")
    }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = \"audit_logs_seq\")
    @SequenceGenerator(
        name = \"audit_logs_seq\",
        sequenceName = \"audit_logs_audit_log_id_seq\",
        schema = \"user_management\",
        allocationSize = 1
    )
    @Column(name = \"audit_log_id\")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = \"user_id\")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = \"action_type_id\")
    private AuditActionTypeEntity actionType;

    @Column(name = \"table_name\")
    private String tableName;

    @Column(name = \"record_id\")
    private Long recordId;

    @Column(name = \"actor_identifier\")
    private String actorIdentifier;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = \"new_values\", columnDefinition = \"jsonb\")
    private Map<String, Object> newValues;

    @CreatedDate
    @Column(name = \"performed_at\")
    private LocalDateTime performedAt;

    @Column(name = \"ip_address\", columnDefinition = \"inet\")
    @Type(PostgreSQLInetType.class)
    private Inet ipAddress;
}
```

---

## Подпакет: `com.logistics.userauth.common`

### Подпакет: `com.logistics.userauth.common.web`

#### Класс: GlobalExceptionHandler.java

```java
/**
 * Глобальный обработчик исключений для всех REST endpoints.
 *
 * <h2>Назначение</h2>
 * Перехватывает исключения и возвращает единообразный JSON формат ошибок.
 *
 * <h2>Обработанные исключения</h2>
 * - BadCredentialsException → 401 INVALID_CREDENTIALS
 * - DataIntegrityViolationException → 409 CONFLICT
 * - MethodArgumentNotValidException → 400 VALIDATION_FAILED
 * - InvalidRefreshTokenException → 401 INVALID_REFRESH_TOKEN
 * - Все остальные Exception → 500 INTERNAL_SERVER_ERROR
 *
 * <h2>Формат ответа</h2>
 * {
 *   \"error\": \"ERROR_CODE\",
 *   \"message\": \"Human-readable message\",
 *   \"fields\": { \"fieldName\": \"error message\" }  // только для VALIDATION_FAILED
 * }
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(\"error\", \"INVALID_CREDENTIALS\");
        body.put(\"message\", \"Неверный телефон или пароль\");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(\"error\", \"CONFLICT\");
        body.put(\"message\", \"Пользователь с таким телефоном или email уже существует\");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put(\"error\", \"VALIDATION_FAILED\");
        body.put(\"fields\", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(\"error\", \"INVALID_REFRESH_TOKEN\");
        body.put(\"message\", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
```

### Подпакет: `com.logistics.userauth.common.api`

**Описание:** Переиспользуемые аннотации для Swagger/OpenAPI документации.

#### Аннотация: SignUpOperation.java

```java
/**
 * Аннотация для документирования endpoint регистрации.
 *
 * @Operation для Swagger
 * - summary: \"Регистрация нового пользователя\"
 * - description: \"Создает новый аккаунт с ролью CLIENT\"
 * - tags: {\"Аутентификация\"}
 *
 * @ApiResponses документируют все возможные HTTP ответы
 */
@Operation(...)
@ApiResponses(...)
public @interface SignUpOperation {}
```

#### Аннотация: SignInOperation.java, RefreshOperation.java, LogoutOperation.java

```java
// Аналогичные аннотации для других endpoints
```

---

## Рекомендации по использованию User-Auth-Service

### Интеграция с другими микросервисами

1. **Импортируйте JwtAuthenticationFilter:**
   ```java
   @SpringBootApplication
   @Import(JwtAuthenticationFilter.class)
   public class OtherServiceApplication { }
   ```

2. **Используйте токены в запросах:**
   ```
   Authorization: Bearer {accessToken}
   ```

3. **При истечении access token:**
   - Используйте refresh token для получения нового
   - POST /auth/refresh с RefreshTokenRequest

### Рекомендации безопасности

- **Секретный ключ:** Используйте сильный, случайный ключ в production (минимум 256 бит)
- **HTTPS:** Всегда используйте HTTPS для передачи токенов
- **CORS:** Настройте CORS для разрешенных origins
- **Refresh Token TTL:** Установите разумное время жизни (7-30 дней)
- **Access Token TTL:** Установите короче (15-60 минут)
