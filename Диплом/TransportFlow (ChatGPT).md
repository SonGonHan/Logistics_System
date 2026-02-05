#диплом
# Краткое описание

Транспортная кампания **TransportCompany** состоит из Spring Boot бэкенда, веб-интерфейса для клиентов/операторов/админов и мобильного Android-приложения для водителей/курьеров на Java. 

---

## Услуги транспортной компании

1. **Грузоперевозки между городами и регионами**
    
    - Стандартные грузы
        
    - Скоропортящиеся товары
        
    - Опасные грузы
		
	- Модерация грузов
		
	- Возможность страховки груза
		 
2.  **Доставка по городу**
	
	- Доставка заказов в ПВЗ
		
	- Доставка заказов до дома получателя
		
3. **Экспресс-доставка**
    
    - Срочные заказы с приоритетной обработкой
        
	- Назначение администратором заказов водителям
		 
4. **Логистическое планирование маршрутов**
    
    - Оптимизация маршрута по времени и стоимости
        
    - Группировка заказов в один рейс
		
	- Система рекомендаций заказов для водителей
		
5. **Услуги складского хранения** 
    
    - Хранение товаров на складах
        

---
## Микросервисы и детали реализации
### 1. **Order Service** (Высокий приоритет)

**Назначение:** Управление заказами (создание, просмотр, изменение статусов, назначение водителей и транспорта).

**Структура:**

- Controller: `OrderController`
    
- Service: `OrderService`
    
- Repository: `OrderRepository`
    

**Таблицы:**

- `orders` (id, client_id, origin, destination, date_shipment, weight, volume, type, status, assigned_vehicle_id, assigned_driver_id)
    
- `assignments` (id, order_id, driver_id, vehicle_id, assigned_at, status)
    

---

### 2. **Identity Service** (Высокий приоритет)

**Назначение:** Аутентификация, авторизация, роли, профили пользователей (клиенты, водители).

**Структура:**

- Controller: `AuthController`, `UserController`
    
- Service: `UserService`
    
- Repository: `UserRepository`
    

**Таблицы:**

- `users` (id, username, email, password_hash, role)
    
- `clients` (id, user_id, company, phone)
    
- `drivers` (id, user_id, license_number, phone, status)
    

---

### 3. **Fleet Service** (Высокий приоритет)

**Назначение:** Управление автопарком и назначением транспорта/водителей на заказы.

**Структура:**

- Controller: `FleetController`
    
- Service: `FleetService`
    
- Repository: `FleetRepository`
    

**Таблицы:**

- `vehicles` (id, plate, type, capacity_weight, capacity_volume, status)
    
- (использует `assignments` из Order Service для связи)
    

---

### 4. **Communication Service** (Средний приоритет)

**Назначение:** Отправка уведомлений и отслеживание геолокации водителей.

**Структура:**

- Controller: `NotificationController`, `TrackingController`
    
- Service: `NotificationService`, `TrackingService`
    
- Repository: `NotificationRepository`, `TrackingRepository`
    

**Таблицы:**

- `notifications` (id, user_id, message, type, sent_at)
    
- `driver_location` (id, driver_id, latitude, longitude, timestamp)
    

---

### 5. **Finance Service** (Средний приоритет)

**Назначение:** Учёт платежей и генерация отчетов.

**Структура:**

- Controller: `PaymentController`, `ReportController`
    
- Service: `PaymentService`, `ReportService`
    
- Repository: `PaymentRepository`, `ReportRepository`
    

**Таблицы:**

- `payments` (id, order_id, client_id, amount, status, paid_at)
    
- (отчёты формируются на основе `orders`, `assignments`, `vehicles`, `drivers`)
    

---

### 6. **Auxiliary Service** (Низкий приоритет)

**Назначение:** Дополнительный функционал (документы, отзывы).

**Структура:**

- Controller: `DocumentController`, `FeedbackController`
    
- Service: `DocumentService`, `FeedbackService`
    
- Repository: `DocumentRepository`, `FeedbackRepository`
    

**Таблицы:**

- `documents` (id, order_id, file_path, uploaded_at)
    
- `feedback` (id, client_id, driver_id, rating, comment, created_at)


---
## Сценарии работы

### 1. Регистрация и авторизация пользователя

1. Пользователь открывает страницу /register или мобильное приложение.
    
2. Отправляет данные на `POST /api/auth/register`.
    
3. Получает подтверждение регистрации.
    
4. Авторизация через `POST /api/auth/login`, получение JWT.
    

### 2. Создание заказа пользователем (1)

1. Пользователь авторизуется в системе.
    
2. Заполняет форму создания заказа на /orders/add-order, выбирая услугу (из списка услуг компании).
	
3. Запрос `POST /api/orders` сохраняет заказ в статусе «Открыт для выбора».
    
4. Заказ доступен в `/api/orders/available` для водителей.
    
### 3. Создание заказа пользователем (2)


### 3. Выбор заказа (Водитель)

1. Водитель заходит в мобильное приложение.
    
2. Запрашивает `GET /api/orders/available`.
    
3. Выбирает заказ -> `PUT /api/orders/{id}/assign-self`.
    
4. Заказ закрепляется за водителем.
    

### 4. Выполнение заказа (Водитель)

1. При выезде водитель отправляет `PUT /api/orders/{id}/status` со значением `IN_PROGRESS`.
    
2. При завершении рейса — тот же endpoint со значением `COMPLETED`.
    
3. Уведомление отправляется клиенту.
    

### 5. Управление автопарком (Оператор/Админ)

1. Открывает /vehicles.
    
2. Выполняет CRUD операции через `/api/vehicles`.
    

### 6. Уведомления

1. При изменении статуса заказа Notification Service отправляет push/SMS/email.
    

### 7. Отчёты (Админ/Оператор)

1. Пользователь запрашивает `/api/reports`.
    
2. Report Service формирует и возвращает данные.
    

### 8. Отзывы (Клиент)

1. После выполнения заказа клиент отправляет `POST /api/feedback`.
---
## Таблица страниц и функционала

| Страница / Раздел     | Пользователь       | Функционал                                      |
| --------------------- | ------------------ | ----------------------------------------------- |
| `/`                   | Все                | Просмотр списка заказов/рейсов (публичная зона) |
| `/login`              | Все                | Авторизация                                     |
| `/register`           | Клиент, Водитель   | Регистрация нового пользователя                 |
| `/orders`             | Клиент             | Просмотр своих заказов, фильтр по статусу       |
| `/orders/available`   | Водитель           | Просмотр доступных заказов, выбор заказа        |
| `/orders/{id}`        | Все авторизованные | Детальная информация о заказе                   |
| `/operator`           | Оператор           | Панель оператора, управление заказами           |
| `/operator/add-order` | Оператор           | Создание нового заказа                          |
| `/driver`             | Водитель           | Личный кабинет водителя, список рейсов          |
| `/driver/history`     | Водитель           | История выполненных рейсов                      |
| `/vehicles`           | Оператор, Админ    | Управление автопарком                           |
| `/admin`              | Админ              | Панель администратора                           |
| `/admin/clients`      | Админ              | Список клиентов, поиск, редактирование          |
| `/admin/operators`    | Админ              | Список операторов, добавление                   |
| `/admin/drivers`      | Админ              | Список водителей, добавление                    |
| `/reports`            | Админ, Оператор    | Просмотр аналитических отчётов                  |
| `/notifications`      | Все                | Просмотр и настройка уведомлений                |
| `/feedback`           | Клиент             | Оставить отзыв                                  |

---
## REST API (основной набор)

### Аутентификация

- `POST /api/auth/register` — регистрация.
    
- `POST /api/auth/login` — авторизация, получение JWT.
    

### Заказы

- `GET /api/orders` — список заказов (фильтры по ролям, статусам, услугам).
    
- `POST /api/orders` — создание заказа (оператор, с указанием услуги).
    
- `GET /api/orders/{id}` — детальная информация.
    
- `PUT /api/orders/{id}` — обновление заказа.
    
- `DELETE /api/orders/{id}` — удаление заказа.
    
- `GET /api/orders/available` — доступные заказы (водитель).
    
- `PUT /api/orders/{id}/assign-self` — взять заказ (водитель).
    
- `PUT /api/orders/{id}/assign` — назначить заказ (оператор/админ).
    
- `PUT /api/orders/{id}/status` — изменение статуса.
    

### Водители

- `GET /api/drivers/{id}` — профиль водителя.
    
- `GET /api/drivers/{id}/history` — история рейсов.
    

### Транспорт

- `GET /api/vehicles` — список транспорта.
    
- `POST /api/vehicles` — добавить транспорт.
    
- `PUT /api/vehicles/{id}` — обновить транспорт.
    
- `DELETE /api/vehicles/{id}` — удалить транспорт.
    

### Уведомления

- `POST /api/notifications` — отправить уведомление.
    
- `GET /api/notifications` — список уведомлений.
    

### Геолокация

- `POST /api/tracking` — передача координат.
    
- `GET /api/tracking/{driverId}` — получение текущей позиции.
    

### Отчёты

- `GET /api/reports` — список доступных отчётов.
    

### Отзывы

- `POST /api/feedback` — добавить отзыв.
    
- `GET /api/feedback/{driverId}` — отзывы о водителе.
---
## Приоритет функций

1. Регистрация, авторизация (JWT)
    
2. CRUD заказов
    
3. Выбор заказа водителем
    
4. Управление транспортом
    
5. Уведомления
    
6. Геолокация (реальное время)
    
7. Отчёты
    
8. Загрузка документов
    
9. Отзывы
    

---
## План работ (обновлённый)

1. Неделя 1–2: Настройка Spring Boot, User Service, Order Service.
    
2. Неделя 3–4: Driver Service (выбор заказа), Vehicle Service.
    
3. Неделя 5: Notification Service.
    
4. Неделя 6: Веб-интерфейс.
    
5. Неделя 7–8: Android-приложение.
    
6. Неделя 9: Tracking, Reports, Feedback.