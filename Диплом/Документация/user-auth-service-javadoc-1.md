# USER-AUTH-SERVICE: ПОЛНАЯ ДОКУМЕНТАЦИЯ (ЧАСТЬ 1/2)

## Пакет: `com.logistics.userauth`

**Описание пакета:**
Основной микросервис аутентификации и управления пользователями. Отвечает за:
- Регистрацию новых пользователей
- Аутентификацию (вход) через телефон + пароль
- Выдачу и валидацию JWT токенов
- Управление сессиями и refresh tokens
- Аудит всех действий с пользователями и их аутентификацией

### Главный класс: UserAuthServiceApplication.java

```java
/**
 * Точка входа микросервиса аутентификации.
 *
 * <h2>Ответственность</h2>
 * - Инициализирует Spring Boot приложение
 * - Подключает shared-library компоненты
 * - Импортирует OpenAPI конфигурацию
 * - Сканирует все @Component, @Service, @Controller в com.logistics.userauth
 *
 * <h2>Архитектура</h2>
 * Используется Clean Architecture (Hexagonal Architecture):
 * <pre>
 * adapter.in      - REST контроллеры, маппреры DTO
 * adapter.out     - JPA entities, репозитории, маппреры персистенса
 * application     - Use Cases, порты, команды
 * domain          - Доменные сущности (User, UserRole и т.д.)
 * infrastructure  - Spring Security, фильтры, конфигурация
 * common          - Глобальные exception handlers, утилиты
 * </pre>
 *
 * <h2>Конфигурация</h2>
 * Читается из application.yml:
 * - server.port: 8080 (или как настроено)
 * - server.servlet.context-path: /api/v1
 * - Credentials для БД (PostgreSQL)
 * - JWT конфигурация (secret, expiration)
 *
 * @author Logistics Team
 * @version 1.0.0
 * @since 2025-12-15
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    \"com.logistics.userauth\",
    \"com.logistics.shared\"
})
@EnableJpaRepositories(basePackages = \"com.logistics.userauth.**.persistence\")
@EntityScan(basePackages = \"com.logistics.userauth.**.persistence\")
@Import(OpenApiConfig.class)
public class UserAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserAuthServiceApplication.class, args);
    }
}
```

---

## Подпакет: `com.logistics.userauth.user`

### Подпакет: `com.logistics.userauth.user.domain`

#### Класс: User.java

```java
/**
 * Доменная сущность пользователя системы логистики.
 *
 * <h2>Назначение</h2>
 * Представляет пользователя с его основной информацией:
 * - Личные данные (ФИО)
 * - Контактные данные (email, телефон)
 * - Аутентификация (пароль в хэшированном виде - НИКОГДА не сырой!)
 * - Роль в системе (CLIENT, COURIER, ADMIN и т.д.)
 * - Статус (ACTIVE, ON_DELETE)
 * - Связь с объектом (склад, ПВЗ)
 *
 * <h2>Примеры ролей</h2>
 * - CLIENT: Обычный клиент, заказывающий доставку
 * - COURIER: Курьер (доставка в пределах города)
 * - DRIVER: Водитель (доставка между городами)
 * - PVZ_OPERATOR: Оператор на стойке выдачи
 * - WAREHOUSE_OPERATOR: Оператор склада (комплектует заказы)
 * - DISPATCHER: Диспетчер (управляет маршрутами)
 * - SYSTEM_ADMIN: Администратор системы
 *
 * @see UserRole для доступных ролей
 * @see UserStatus для доступных статусов
 * @see UserEntity для JPA representation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    private String phone;
    private String passwordHash;       // ТОЛЬКО ХЕШИРОВАН!
    private String firstName;
    private String lastName;
    private String middleName;
    private UserRole role;
    private Long facilityId;
    private LocalDateTime createdTime;
    private LocalDateTime lastAccessedTime;
    private UserStatus status;
}
```

#### Enum: UserRole.java

```java
/**
 * Перечисление ролей пользователей в системе логистики.
 *
 * <h2>Роли и их значение</h2>
 *
 * Клиенты:
 * - CLIENT: Обычный клиент, заказывающий доставку
 *
 * ПВЗ (Пункт выдачи):
 * - PVZ_OPERATOR: Оператор на стойке выдачи
 * - PVZ_ADMIN: Администратор ПВЗ
 *
 * Доставка:
 * - COURIER: Курьер (доставка в пределах города)
 * - DRIVER: Водитель (доставка между городами)
 *
 * Логистика:
 * - DISPATCHER: Диспетчер (управляет маршрутами и доставками)
 *
 * Склады:
 * - WAREHOUSE_OPERATOR: Оператор склада (комплектует)
 * - WAREHOUSE_ADMIN: Администратор склада
 *
 * Администрация:
 * - HR: HR менеджер
 * - ACCOUNTANT: Бухгалтер
 * - SYSTEM_ADMIN: Администратор системы (полные права)
 * - SYSTEM: Специальная роль для системных операций
 * - UNREGISTERED_CONTACT: Контакт, еще не зарегистрировавшийся
 *
 * <h2>Spring Security интеграция</h2>
 * Преобразуется в ROLE_COURIER, ROLE_CLIENT и т.д.
 * для использования в @PreAuthorize и других security аннотациях.
 *
 * @see User для сущности пользователя
 * @see LogisticsUserDetails для Spring Security интеграции
 */
public enum UserRole {
    UNREGISTERED_CONTACT,
    CLIENT,
    PVZ_OPERATOR,
    PVZ_ADMIN,
    COURIER,
    DRIVER,
    DISPATCHER,
    WAREHOUSE_OPERATOR,
    WAREHOUSE_ADMIN,
    HR,
    ACCOUNTANT,
    SYSTEM_ADMIN,
    SYSTEM
}
```

#### Enum: UserStatus.java

```java
/**
 * Перечисление статусов пользователя.
 *
 * <h2>Статусы</h2>
 * - ACTIVE: Пользователь активен и может использовать систему
 * - ON_DELETE: Пользователь запросил удаление
 *             (удаление выполнится на следующий день, дается время на отмену)
 *
 * <h2>Процесс удаления пользователя</h2>
 * 1. Пользователь инициирует запрос на удаление
 * 2. Статус меняется на ON_DELETE
 * 3. Пользователю отправляется подтверждение по email
 * 4. В течение 24 часов пользователь может отменить запрос
 * 5. После 24 часов пользователь удаляется необратимо
 *
 * @see User для сущности пользователя
 */
public enum UserStatus {
    ACTIVE,
    ON_DELETE
}
```

### Подпакет: `com.logistics.userauth.user.application.port.out`

#### Интерфейс: UserRepository.java

```java
/**
 * Порт (интерфейс) для работы с хранилищем пользователей.
 *
 * <h2>Назначение</h2>
 * Определяет контракт для всех операций с пользователями,
 * не привязываясь к конкретной реализации (JPA, MongoDB и т.д.).
 *
 * <h2>Реализации</h2>
 * - UserPersistenceAdapter (текущая - JPA)
 * - Может быть заменена на другую реализацию при необходимости
 *
 * <h2>Методы</h2>
 * - save(user) - Сохранить или обновить пользователя
 * - delete(user) - Удалить пользователя
 * - findById(id) - Найти по ID
 * - findByPhone(phone) - Найти по телефону (уникален)
 * - findByEmail(email) - Найти по email
 * - findByRole(role) - Найти первого пользователя с ролью
 * - findByFacilityId(id) - Найти по объекту (склад, ПВЗ)
 *
 * @see UserPersistenceAdapter для реализации на JPA
 * @see User для доменной сущности
 */
public interface UserRepository {
    User save(User user);
    void delete(User user);
    Optional<User> findById(Long id);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(UserRole role);
    Optional<User> findByFacilityId(long id);
}
```

### Подпакет: `com.logistics.userauth.user.adapter.out.persistence`

#### Класс: UserEntity.java

```java
/**
 * JPA сущность для хранения пользователей в БД.
 *
 * <h2>Таблица в БД</h2>
 * Schema: user_management
 * Table: users
 *
 * Уникальность: phone UNIQUE
 * Индексы: email, phone, role_name, last_accessed_at, facility_id
 *
 * @see UserJpaRepository для работы с БД
 * @see UserPersistenceMapper для преобразования Domain ↔ Entity
 */
@Entity
@Table(
    name = \"users\",
    schema = \"user_management\",
    uniqueConstraints = @UniqueConstraint(columnNames = \"phone\"),
    indexes = {
        @Index(columnList = \"email\", name = \"idx_users_email\"),
        @Index(columnList = \"phone\", name = \"idx_users_phone\"),
        @Index(columnList = \"role_name\", name = \"idx_users_role_name\"),
        @Index(columnList = \"last_accessed_at\", name = \"idx_users_last_accessed\"),
        @Index(columnList = \"facility_id\", name = \"idx_users_facility_id\")
    }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = \"users_seq\")
    @SequenceGenerator(
        name = \"users_seq\",
        sequenceName = \"users_user_id_seq\",
        schema = \"user_management\",
        allocationSize = 1
    )
    @Column(name = \"user_id\")
    private Long id;

    @Column(name = \"email\")
    @Email
    private String email;

    @Column(name = \"phone\", nullable = false)
    private String phone;

    @Column(name = \"password_hash\")
    private String passwordHash;

    @Column(name = \"first_name\", nullable = false)
    private String firstName;

    @Column(name = \"last_name\", nullable = false)
    private String lastName;

    @Column(name = \"middle_name\")
    private String middleName;

    @Enumerated(EnumType.STRING)
    @Column(name = \"role_name\", nullable = false)
    private UserRole role;

    @Column(name = \"facility_id\")
    private Long facilityId;

    @Enumerated(EnumType.STRING)
    @Column(name = \"user_status\")
    private UserStatus status;

    @CreatedDate
    @Column(name = \"created_at\", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = \"last_accessed_at\")
    private LocalDateTime lastAccessedAt;
}
```

#### Интерфейс: UserJpaRepository.java

```java
/**
 * Spring Data JPA репозиторий для работы с пользователями.
 *
 * <h2>Методы</h2>
 * Наследует от JpaRepository:
 * - save, saveAll, delete, deleteAll, findById, findAll и т.д.
 *
 * Плюс кастомные методы для поиска:
 * - findByEmail(email)
 * - findByPhone(phone)
 * - findByRole(role)
 * - findByFacilityId(id)
 *
 * @see UserEntity для сущности
 * @see UserPersistenceAdapter для использования в бизнес-логике
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhone(String phone);
    Optional<UserEntity> findByRole(UserRole role);
    Optional<UserEntity> findByFacilityId(long id);
}
```

#### Класс: UserPersistenceMapper.java

```java
/**
 * Маппер для преобразования между Domain и JPA Entity.
 *
 * <h2>Назначение</h2>
 * Конвертирует User (доменная модель) ↔ UserEntity (JPA entity).
 * Позволяет скрыть детали БД от бизнес-слоя.
 *
 * @see User для доменной модели
 * @see UserEntity для JPA entity
 */
@Component
public class UserPersistenceMapper {
    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
            .id(domain.getId())
            .email(domain.getEmail())
            .phone(domain.getPhone())
            .passwordHash(domain.getPasswordHash())
            .firstName(domain.getFirstName())
            .lastName(domain.getLastName())
            .middleName(domain.getMiddleName())
            .role(domain.getRole())
            .facilityId(domain.getFacilityId())
            .status(domain.getStatus())
            .createdAt(domain.getCreatedTime())
            .lastAccessedAt(domain.getLastAccessedTime())
            .build();
    }

    public User toDomain(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .phone(entity.getPhone())
            .passwordHash(entity.getPasswordHash())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .middleName(entity.getMiddleName())
            .role(entity.getRole())
            .facilityId(entity.getFacilityId())
            .status(entity.getStatus())
            .createdTime(entity.getCreatedAt())
            .lastAccessedTime(entity.getLastAccessedAt())
            .build();
    }
}
```

#### Класс: UserPersistenceAdapter.java

```java
/**
 * Адаптер, реализующий интерфейс UserRepository для JPA.
 *
 * <h2>Паттерн</h2>
 * Это реализация Adapter паттерна:
 * - Интерфейс UserRepository определяет контракт
 * - UserPersistenceAdapter реализует этот контракт с помощью JPA
 * - Бизнес-логика зависит от интерфейса, а не от реализации
 *
 * <h2>Преимущества</h2>
 * - Если позже нужна другая БД (MongoDB, Redis), создаем новый адаптер
 * - Бизнес-логика не меняется
 * - Легче тестировать (подменить mock-адаптер)
 *
 * @implements UserRepository
 * @see UserRepository для контракта
 * @see UserJpaRepository для JPA работы
 */
@RequiredArgsConstructor
@Component
public class UserPersistenceAdapter implements UserRepository {
    private final UserJpaRepository jpaRepo;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        var saved = jpaRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(User user) {
        UserEntity entity = mapper.toEntity(user);
        jpaRepo.delete(entity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaRepo.findByPhone(phone).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByRole(UserRole role) {
        return jpaRepo.findByRole(role).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByFacilityId(long id) {
        return jpaRepo.findByFacilityId(id).map(mapper::toDomain);
    }
}
```

### Подпакет: `com.logistics.userauth.user.adapter.in.web.dto`

#### Класс: SignUpRequest.java

```java
/**
 * Запрос для регистрации нового пользователя.
 *
 * <h2>Валидация</h2>
 * - email: Должен быть в формате email
 * - phone: Формат +7XXXXXXXXXX (РФ), +375XXXXXXXXX (БР), +77XXXXXXXXX (КЗ)
 * - password: Минимум 8 символов, цифра, заглавная, строчная, спецсимвол
 * - firstName, lastName: Не пусты
 *
 * <h2>Пример запроса</h2>
 * {
 *   \"email\": \"john@example.com\",
 *   \"phone\": \"+79991234567\",
 *   \"password\": \"Password123!\",
 *   \"firstName\": \"Иван\",
 *   \"lastName\": \"Иванов\",
 *   \"middleName\": \"Иванович\"
 * }
 */
@Builder
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

    String middleName
) {}
```

#### Класс: SignInRequest.java

```java
/**
 * Запрос для авторизации пользователя.
 *
 * <h2>Валидация</h2>
 * - phone: Обязателен, формат +7/375/77...
 * - password: Обязателен, сложный пароль
 * - email: Опциональный альтернативный способ входа
 *
 * <h2>Пример запроса</h2>
 * {
 *   \"phone\": \"+79991234567\",
 *   \"password\": \"Password123!\"
 * }
 */
@Builder
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

#### Класс: UserDTO.java

```java
/**
 * DTO для передачи информации о пользователе в ответах.
 *
 * <h2>Назначение</h2>
 * Содержит публичную информацию пользователя для отправки клиенту.
 * НЕ содержит чувствительной информации (пароль, passwordHash).
 *
 * <h2>Примеры</h2>
 * {
 *   \"phone\": \"+79991234567\",
 *   \"firstName\": \"Иван\",
 *   \"lastName\": \"Иванов\",
 *   \"role\": \"CLIENT\"
 * }
 */
@Builder
public record UserDTO(
    String phone,
    String firstName,
    String lastName,
    String middleName,
    UserRole role
) {}
```

#### Класс: FacilityDTO.java

```java
/**
 * DTO для информации об объекте (ПВЗ, склад).
 *
 * @Builder
 */
public record FacilityDTO(String name, String address) {}
```

### Подпакет: `com.logistics.userauth.user.adapter.in`

#### Класс: UserControllerMapper.java

```java
/**
 * Маппер для преобразования между Domain User и DTO.
 *
 * <h2>Назначение</h2>
 * Конвертирует User → UserDTO и обратно.
 * НЕ передает пароль в DTO (по соображениям безопасности).
 *
 * @see UserDTO для DTO
 * @see User для доменной сущности
 */
@Component
public class UserControllerMapper {
    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
            .phone(user.getPhone())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .middleName(user.getMiddleName())
            .role(user.getRole())
            .build();
    }

    public static User toUser(UserDTO userDTO) {
        return User.builder()
            .phone(userDTO.phone())
            .firstName(userDTO.firstName())
            .lastName(userDTO.lastName())
            .middleName(userDTO.middleName())
            .role(userDTO.role())
            .lastAccessedTime(LocalDateTime.now())
            .build();
    }
}
```

---

## Подпакет: `com.logistics.userauth.auth.jwt`

### Подпакет: `com.logistics.userauth.auth.jwt.application.port.in`

**Описание:** Use cases (порты входящих зависимостей) для аутентификации и работы с токенами.

#### Интерфейс: AuthenticateUserUseCase.java

```java
/**
 * Use Case для аутентификации пользователя (вход в систему).
 *
 * <h2>Процесс</h2>
 * 1. Получает команду с телефоном и паролем
 * 2. Проверяет учетные данные
 * 3. Выдает access token и refresh token
 *
 * <h2>Реализация</h2>
 * AuthenticateUserService
 *
 * @see AuthenticateUserService для реализации
 */
public interface AuthenticateUserUseCase {
    JwtAuthenticationResponse authenticate(AuthenticateUserCommand command);
}
```

#### Интерфейс: RegisterUserUseCase.java

```java
/**
 * Use Case для регистрации нового пользователя.
 *
 * <h2>Процесс</h2>
 * 1. Получает команду с данными пользователя
 * 2. Проверяет уникальность телефона/email
 * 3. Создает новый аккаунт с ролью CLIENT
 * 4. Выдает access token и refresh token
 *
 * <h2>Реализация</h2>
 * RegisterUserService
 *
 * @see RegisterUserService для реализации
 */
public interface RegisterUserUseCase {
    JwtAuthenticationResponse register(RegisterUserCommand command);
}
```

#### Интерфейс: RefreshAccessTokenUseCase.java

```java
/**
 * Use Case для обновления access токена используя refresh token.
 *
 * <h2>Процесс (Token Rotation)</h2>
 * 1. Получает старый refresh token
 * 2. Проверяет его валидность
 * 3. Помечает старый токен как отозванный
 * 4. Выдает новый access token и новый refresh token
 *
 * Это повышает безопасность: если refresh token будет скомпрометирован,
 * злоумышленник сможет его использовать только один раз.
 *
 * <h2>Реализация</h2>
 * RefreshAccessTokenService
 *
 * @see RefreshAccessTokenService для реализации
 */
public interface RefreshAccessTokenUseCase {
    JwtAuthenticationResponse refresh(RefreshAccessTokenCommand command);
}
```

#### Интерфейс: RevokeRefreshTokenUseCase.java

```java
/**
 * Use Case для отзыва (revoke) refresh токенов.
 *
 * <h2>Назначение</h2>
 * Отзыв токена делает его непригодным для дальнейшего использования.
 * Используется при logout и других операциях завершения сессии.
 *
 * <h2>Процесс</h2>
 * 1. Получает refresh token
 * 2. Находит соответствующую сессию
 * 3. Помечает сессию как revoked = true
 *
 * При попытке использовать отозванный токен для обновления access token
 * будет выброшено исключение InvalidRefreshTokenException.
 *
 * <h2>Реализация</h2>
 * RevokeRefreshTokenService
 *
 * @see RevokeRefreshTokenService для реализации
 */
public interface RevokeRefreshTokenUseCase {
    void revoke(RevokeRefreshTokenCommand command);
}
```

#### Интерфейс: InternalCreateRefreshTokenUseCase.java

```java
/**
 * Internal Use Case для создания refresh токенов.
 *
 * <h2>Назначение</h2>
 * Отмечен как \"Internal\" потому что:
 * - Не должен вызваться напрямую из контроллеров
 * - Используется другими use cases (Register, Authenticate, Refresh)
 * - Инкапсулирует логику создания и сохранения сессии
 *
 * <h2>Процесс</h2>
 * 1. Генерирует случайный UUID для refresh токена
 * 2. Создает сессию пользователя с TTL
 * 3. Привязывает к IP и User-Agent (для защиты)
 * 4. Сохраняет в БД
 * 5. Возвращает токен строку
 *
 * <h2>Реализация</h2>
 * InternalCreateRefreshTokenService
 *
 * @see InternalCreateRefreshTokenService для реализации
 */
public interface InternalCreateRefreshTokenUseCase {
    String create(CreateRefreshTokenCommand command);
}
```

### Подпакет: `com.logistics.userauth.auth.jwt.application.port.in.command`

**Описание:** Команды для use cases (CQRS pattern).

#### Класс: AuthenticateUserCommand.java

```java
/**
 * Команда для аутентификации пользователя.
 *
 * @Builder
 */
public record AuthenticateUserCommand(
    String phone,
    String password,
    String ipAddress,
    String userAgent
) {}
```

#### Класс: RegisterUserCommand.java

```java
/**
 * Команда для регистрации пользователя.
 *
 * @Builder
 */
public record RegisterUserCommand(
    String email,
    String phone,
    String rawPassword,
    String firstName,
    String lastName,
    String middleName,
    String ipAddress,
    String userAgent
) {}
```

#### Класс: RefreshAccessTokenCommand.java

```java
/**
 * Команда для обновления access токена.
 *
 * @Builder
 */
public record RefreshAccessTokenCommand(
    String refreshToken,
    String ipAddress,
    String userAgent
) {}
```

#### Класс: RevokeRefreshTokenCommand.java

```java
/**
 * Команда для отзыва refresh токена.
 *
 * @Builder
 */
public record RevokeRefreshTokenCommand(String refreshToken) {}
```

#### Класс: CreateRefreshTokenCommand.java

```java
/**
 * Команда для создания refresh токена.
 *
 * @Builder
 */
public record CreateRefreshTokenCommand(
    Long userId,
    String ipAddress,
    String userAgent
) {}
```

### Подпакет: `com.logistics.userauth.auth.jwt.application.port.out`

#### Интерфейс: TokenGeneratorPort.java

```java
/**
 * Порт для генерации и валидации JWT токенов.
 *
 * <h2>Методы</h2>
 * - generateAccessToken(user) - Создать новый access token
 * - isTokenValid(token) - Проверить валидность токена
 * - extractUserId(token) - Получить userId из токена
 *
 * <h2>Реализация</h2>
 * JwtTokenProvider
 *
 * @see JwtTokenProvider для реализации
 */
public interface TokenGeneratorPort {
    String generateAccessToken(User user);
    boolean isTokenValid(String token);
    Long extractUserId(String token);
}
```

### Подпакет: `com.logistics.userauth.auth.jwt.application.usecase`

**Описание:** Реализации use cases (бизнес-логика).

#### Класс: AuthenticateUserService.java

```java
/**
 * Сервис для аутентификации пользователя.
 *
 * <h2>Процесс</h2>
 * 1. Находит пользователя по телефону
 * 2. Проверяет пароль используя PasswordEncoder
 * 3. Генерирует access token
 * 4. Создает refresh token
 * 5. Возвращает оба токена в ответе
 *
 * <h2>Исключения</h2>
 * - BadCredentialsException: Если телефон не найден или пароль неверен
 *
 * @implements AuthenticateUserUseCase
 */
@Service
@RequiredArgsConstructor
public class AuthenticateUserService implements AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final InternalCreateRefreshTokenUseCase createRefreshTokenUseCase;

    @Override
    public JwtAuthenticationResponse authenticate(AuthenticateUserCommand command) {
        var user = userRepository.findByPhone(command.phone())
            .orElseThrow(() -> new BadCredentialsException(\"Invalid credentials\"));

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new BadCredentialsException(\"Invalid credentials\");
        }

        var accessToken = tokenGenerator.generateAccessToken(user);
        var refreshToken = createRefreshTokenUseCase.create(
            CreateRefreshTokenCommand.builder()
                .userId(user.getId())
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .build()
        );
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }
}
```

[ПРОДОЛЖЕНИЕ СЛЕДУЕТ В СЛЕДУЮЩЕМ ФАЙЛЕ...]
