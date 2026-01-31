# Grafana Configuration

## Доступ к Grafana

- **URL**: http://localhost:3001
- **Логин**: admin (по умолчанию, можно изменить в .env: `GRAFANA_ADMIN_USER`)
- **Пароль**: admin (по умолчанию, можно изменить в .env: `GRAFANA_ADMIN_PASSWORD`)

При первом входе Grafana попросит сменить пароль.

## Автоматическая настройка

При запуске автоматически подключается источник данных:
- **Elasticsearch** - http://elasticsearch:9200
- Индекс: `filebeat-*`
- Поле времени: `@timestamp`

## Создание дашбордов

### Для логов приложений:

1. Зайдите в Grafana → Dashboards → New Dashboard
2. Add visualization → Выберите Elasticsearch
3. Настройте запрос:
   - Query: `container.name:"logistics-*"`
   - Metric: Count
   - Group by: @timestamp
4. В визуализации выберите "Logs" для просмотра логов

### Популярные запросы:

**Логи user-auth-service:**
```
container.name:"logistics-user-auth-service"
```

**Логи core-business-service:**
```
container.name:"logistics-core-business-service"
```

**Ошибки (ERROR level):**
```
log.level:"ERROR" AND container.name:"logistics-*"
```

**Конкретный пользователь в логах:**
```
message:*user_id* AND container.name:"logistics-*"
```

## Полезные плагины

Уже установлены:
- grafana-clock-panel - панель с часами
- grafana-simple-json-datasource - для JSON данных

Дополнительные плагины можно установить через переменную окружения:
```yaml
GF_INSTALL_PLUGINS=plugin1,plugin2,plugin3
```

## Структура папок

```
docker/grafana/
├── datasources/          # Автоматическая настройка источников данных
│   └── elasticsearch.yml # Конфигурация Elasticsearch
└── README.md            # Эта инструкция
```

## Интеграция с ELK

Grafana работает параллельно с Kibana:
- **Kibana** (http://localhost:5601) - для детального анализа логов и поиска
- **Grafana** (http://localhost:3001) - для дашбордов, мониторинга и алертов

Оба инструмента используют один Elasticsearch, но имеют разные интерфейсы и возможности.