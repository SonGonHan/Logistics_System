# ПОЛНАЯ ДОКУМЕНТАЦИЯ JAVA-ПРОЕКТА LOGISTICS SYSTEM
## Содержание и структура всех файлов

---

## 📋 ФАЙЛ 1: DB-MIGRATION (db-migration-javadoc.md)

### Содержит полную документацию для модуля инициализации БД

**Пакеты:**
- `com.logistics.db`

**Классы:**
1. **DbMigrationApplication.java**
   - Точка входа для инициализации БД
   - Использует Flyway для миграций
   - Логирует ход выполнения, обрабатывает ошибки

2. **FlywayConfig.java**
   - Конфигурация Flyway
   - Сканирует миграции из classpath:db/migration
   - Валидирует целостность схемы БД
   - Защита от случайного удаления БД (cleanDisabled: true)

**Ключевые концепции:**
- Versioning БД через Flyway
- Идемпотентные миграции (IF NOT EXISTS)
- Структура файлов миграций (V1__, V2__, и т.д.)

---

## 📋 ФАЙЛ 2: SHARED-LIBRARY (shared-library-javadoc.md)

### Содержит полную документацию для общей библиотеки

**Пакеты:**
- `com.logistics.shared`
- `com.logistics.shared.validation`
- `com.logistics.shared.audit_action`
- `com.logistics.shared.audit_action.domain`
- `com.logistics.shared.audit_action.persistence`

**Подпакет: validation (кастомные валидаторы)**

1. **Phone.java** (Аннотация)
   - Валидация телефонных номеров
   - Поддержка: Россия (+7), Беларусь (+375), Казахстан (+77)
   - С форматированием (+7 (999) 123-45-67)

2. **PhoneValidator.java** (Реализация)
   - Нормализует номер (удаляет пробелы, скобки, дефисы)
   - Проверяет по regex парттерну для каждой страны

3. **Password.java** (Аннотация)
   - Требования: минимум 8 символов
   - Заглавная, строчная буквы, цифра, спецсимвол

4. **PasswordValidator.java** (Реализация)
   - Использует positive lookahead assertions
   - Строгая проверка сложности пароля

**Подпакет: audit_action (типы аудит-действий)**

1. **AuditActionType.java** (Доменная модель)
   - id, actionName, category, description
   - Примеры: USER_LOGIN, ORDER_CREATED, PROFILE_UPDATED

2. **AuditActionTypeEntity.java** (JPA Entity)
   - Таблица: shared_data.audit_action_types
   - SMALLSERIAL ID, UNIQUE action_name

3. **AuditActionJpaRepository.java** (JPA Репозиторий)
   - findByActionName(actionName)
   - findByCategory(category)

4. **AuditActionTypeMapper.java** (Entity ↔ Domain маппер)
   - toDomain(entity)
   - toEntity(domain)

5. **AuditActionTypeService.java** (Бизнес-сервис)
   - getActionTypeById(id)
   - getActionTypeActionName(actionName)
   - getActionTypeByCategory(category)

**Конфигурация:**

1. **SharedLibraryConfiguration.java**
   - @EntityScan и @EnableJpaRepositories
   - Регистрирует компоненты shared-library

2. **OpenApiConfig.java**
   - Единая конфигурация Swagger для всех сервисов
   - Bearer JWT поддержка
   - Контактная информация и версия API

---

## 📋 ФАЙЛ 3A: USER-AUTH-SERVICE ЧАСТЬ 1 (user-auth-service-javadoc-1.md)

### Первая половина документации микросервиса аутентификации

**Главный класс:**
- `UserAuthServiceApplication.java`

**Подпакет: user (управление пользователями)**

1. **User.java** (Доменная сущность)
   - id, email, phone, passwordHash
   - firstName, lastName, middleName
   - role (enum UserRole), status (enum UserStatus)
   - facilityId (для привязки к объекту - склад, ПВЗ и т.д.)

2. **UserRole.java** (Enum)
   - CLIENT, COURIER, DRIVER
   - PVZ_OPERATOR, PVZ_ADMIN
   - WAREHOUSE_OPERATOR, WAREHOUSE_ADMIN
   - HR, ACCOUNTANT, SYSTEM_ADMIN, SYSTEM
   - UNREGISTERED_CONTACT (для контактов, еще не регистрировавшихся)

3. **UserStatus.java** (Enum)
   - ACTIVE: пользователь активен
   - ON_DELETE: запрошено удаление (удалится через 24 часа)

**Интерфейс: UserRepository.java** (Порт выходящей зависимости)
- save(user)
- delete(user)
- findById(id)
- findByPhone(phone)
- findByEmail(email)
- findByRole(role)
- findByFacilityId(id)

**Подпакет: adapter.out.persistence (JPA)**

1. **UserEntity.java** (JPA Entity)
   - Schema: user_management, Table: users
   - Индексы: email, phone, role_name, last_accessed_at, facility_id
   - UNIQUE constraint на phone

2. **UserJpaRepository.java** (Spring Data JPA)
   - Кастомные методы для поиска

3. **UserPersistenceMapper.java** (Entity ↔ Domain)
   - toEntity(domain)
   - toDomain(entity)

4. **UserPersistenceAdapter.java** (Реализация UserRepository)
   - Adapter паттерн для JPA

**Подпакет: adapter.in.web.dto (DTO)**

1. **SignUpRequest.java**
   - email, phone, password
   - firstName, lastName, middleName
   - С валидацией @Email, @Phone, @Password

2. **SignInRequest.java**
   - phone, email (опционально), password
   - С валидацией

3. **UserDTO.java**
   - Публичная информация (БЕЗ пароля!)
   - phone, firstName, lastName, middleName, role

4. **FacilityDTO.java**
   - name, address

**Подпакет: adapter.in (Web адаптер)**

- **UserControllerMapper.java**
  - User ↔ UserDTO преобразование

**Подпакет: auth.jwt.application.port.in (Use Cases)**

1. **AuthenticateUserUseCase.java** (Интерфейс)
   - Аутентификация пользователя (вход)

2. **RegisterUserUseCase.java** (Интерфейс)
   - Регистрация нового пользователя

3. **RefreshAccessTokenUseCase.java** (Интерфейс)
   - Обновление access токена (token rotation)

4. **RevokeRefreshTokenUseCase.java** (Интерфейс)
   - Отзыв refresh токена (logout)

5. **InternalCreateRefreshTokenUseCase.java** (Интерфейс)
   - Создание refresh токена (внутренний use case)

**Подпакет: auth.jwt.application.port.in.command (CQRS Команды)**

- AuthenticateUserCommand
- RegisterUserCommand
- RefreshAccessTokenCommand
- RevokeRefreshTokenCommand
- CreateRefreshTokenCommand

**Подпакет: auth.jwt.application.port.out (Выходящие порты)**

- **TokenGeneratorPort.java**
  - generateAccessToken(user)
  - isTokenValid(token)
  - extractUserId(token)

---

## 📋 ФАЙЛ 3B: USER-AUTH-SERVICE ЧАСТЬ 2 (user-auth-service-javadoc-2.md)

### Вторая половина документации микросервиса аутентификации

**Подпакет: auth.jwt.application.usecase (Реализации Use Cases)**

1. **AuthenticateUserService.java**
   - Находит пользователя по телефону
   - Проверяет пароль (BCrypt)
   - Генерирует access token
   - Создает refresh token

2. **RegisterUserService.java**
   - Создает пользователя с ролью CLIENT
   - Хэширует пароль
   - Проверяет уникальность телефона/email
   - Выдает токены

3. **RefreshAccessTokenService.java**
   - Token Rotation паттерн
   - Помечает старый токен как revoked
   - Выдает новый access + refresh token
   - @Transactional для атомарности

4. **RevokeRefreshTokenService.java**
   - Находит сессию по refresh токену
   - Помечает как revoked = true

5. **InternalCreateRefreshTokenService.java**
   - Генерирует UUID для refresh токена
   - Создает UserSession с TTL
   - Привязывает к IP и User-Agent
   - Сохраняет в БД

**Подпакет: auth.jwt.application.exception**

- **InvalidRefreshTokenException.java**
  - Выбрасывается при невалидном/отозванном/истекшем токене
  - Возвращает 401 Unauthorized

**Подпакет: auth.jwt.adapter.in.web (REST контроллер)**

1. **AuthController.java**
   - POST /auth/sign-up - регистрация (201 CREATED)
   - POST /auth/sign-in - вход (200 OK)
   - POST /auth/refresh - обновление токена (200 OK)
   - POST /auth/logout - выход (204 NO CONTENT)

2. **JwtAuthenticationResponse.java** (DTO)
   - accessToken: JWT для доступа к защищенным ресурсам
   - refreshToken: UUID для получения нового access token

3. **RefreshTokenRequest.java** (DTO)
   - refreshToken: для /refresh и /logout endpoints

**Подпакет: auth.jwt.adapter.in.security (Фильтр)**

- **JwtAuthenticationFilter.java**
  - Применяется ко ВСЕМ запросам (кроме /auth/**)
  - Читает Authorization header
  - Извлекает Bearer токен
  - Валидирует через TokenGeneratorPort
  - Загружает пользователя и устанавливает в SecurityContext

**Подпакет: auth.jwt.adapter.out (JWT провайдер)**

- **JwtTokenProvider.java**
  - HS256 (HMAC SHA256) подписание
  - generateAccessToken(user)
  - isTokenValid(token)
  - extractUserId(token)
  - Читает конфиг из application.yml

**Подпакет: auth.session.domain**

- **UserSession.java** (Доменная сущность)
  - id, user, refreshToken
  - createdAt, expiresAt
  - revoked (была ли отозвана сессия)
  - ipAddress, userAgent (для защиты)

**Подпакет: audit.domain**

- **AuditLog.java** (Доменная сущность)
  - id, user, actionType
  - tableName, recordId (для log всех операций)
  - actorIdentifier (email/телефон)
  - newValues (JSONB с изменениями)
  - performedAt, ipAddress

**Подпакет: audit.app.out**

- **AuditLogRepository.java** (Порт)
  - save(auditLog)
  - delete(auditLog)
  - findByUser(user)
  - findByActionType(actionType)
  - findByActorIdentifier(actorIdentifier)

**Подпакет: audit.adapter.out.persistence**

- **AuditLogEntity.java** (JPA Entity)
  - Schema: user_management, Table: audit_logs
  - JSONB для newValues
  - inet PostgreSQL тип для IP

**Подпакет: common.web**

- **GlobalExceptionHandler.java**
  - BadCredentialsException → 401 INVALID_CREDENTIALS
  - DataIntegrityViolationException → 409 CONFLICT
  - MethodArgumentNotValidException → 400 VALIDATION_FAILED
  - InvalidRefreshTokenException → 401 INVALID_REFRESH_TOKEN

**Подпакет: common.api**

- **SignUpOperation.java** (Аннотация Swagger)
- **SignInOperation.java** (Аннотация Swagger)
- **RefreshOperation.java** (Аннотация Swagger)
- **LogoutOperation.java** (Аннотация Swagger)

---

## 🔗 СВЯЗИ МЕЖДУ МОДУЛЯМИ

```
┌──────────────────────────────────────────────────────────────┐
│                    SHARED-LIBRARY                            │
│  ┌─────────────────┐  ┌────────────────┐  ┌──────────────┐  │
│  │ Валидаторы      │  │ OpenAPI конфиг │  │ Аудит типы   │  │
│  │ @Phone, @Password │ │ Swagger UI     │  │ (shared_data)│  │
│  └─────────────────┘  └────────────────┘  └──────────────┘  │
└──────────────────────────────────────────────────────────────┘
         ↓ (импортируется)            ↓ (используется)
┌──────────────────────────────────────────────────────────────┐
│              USER-AUTH-SERVICE                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Пользователи (domain.User)                           │   │
│  │   + Валидация телефона, пароля                      │   │
│  │   + Разные роли (CLIENT, COURIER и т.д.)           │   │
│  │   + JWT токены (access + refresh)                   │   │
│  │   + Аудит логирование (audit_logs)                  │   │
│  └──────────────────────────────────────────────────────┘   │
│  REST Endpoints:                                              │
│    POST /auth/sign-up    - Регистрация                       │
│    POST /auth/sign-in    - Вход                              │
│    POST /auth/refresh    - Обновление токена                 │
│    POST /auth/logout     - Выход                             │
└──────────────────────────────────────────────────────────────┘
         ↓ (использует)
┌──────────────────────────────────────────────────────────────┐
│                    DB-MIGRATION                              │
│  Flyway миграции (V1__, V2__, и т.д.)                        │
│  Создание схем: shared_data, user_management                 │
└──────────────────────────────────────────────────────────────┘
```

---

## 🗄️ СТРУКТУРА БД

### Схемы
- **shared_data** - общие данные для всех микросервисов
  - audit_action_types (типы действий для аудита)

- **user_management** - управление пользователями
  - users (пользователи)
  - user_sessions (активные сессии с refresh tokens)
  - audit_logs (логирование всех действий)

---

## 📝 АРХИТЕКТУРА: Clean Architecture (Hexagonal)

```
              ┌────────────────────────┐
              │  adapter.in            │
              │ (REST контроллеры)     │
              └────────────┬───────────┘
                           │
              ┌────────────┴───────────┐
              │  application.port.in   │
              │ (Use Cases)            │
              └────────────┬───────────┘
                           │
     ┌─────────────────────┴────────────────────────┐
     │  application.usecase                         │
     │ (Реализации use cases)                       │
     └──────┬──────────────────────────────────────┘
            │
     ┌──────┴──────┬──────────────────┐
     │             │                  │
┌────┴─────┐  ┌───┴──────┐  ┌────────┴───────┐
│ domain   │  │application.port.out        │
│ (Сущности)  │ (Порты выходящих)      │
└──────────┘  └─────────┬──────────────┘
                    │
              ┌─────┴──────┐
              │ adapter.out│
              │(Persistence)
              └────────────┘
```

Каждый слой зависит только от интерфейсов (портов), не от реализаций. 
Это позволяет легко менять реализации (БД, API и т.д.) без изменения бизнес-логики.

---

## 🚀 ЗАПУСК ПРОЕКТА

### 1. Миграции БД (первый запуск)
```bash
docker-compose run --rm db-migration
```

### 2. Запуск микросервиса аутентификации
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### 3. Доступ к API
- REST API: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/v1/v3/api-docs

---

## 📚 ДОПОЛНИТЕЛЬНЫЕ РЕСУРСЫ

- **JWT токены:** https://jwt.io
- **Spring Security:** https://spring.io/projects/spring-security
- **Flyway миграции:** https://flywaydb.org
- **Clean Architecture:** https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
