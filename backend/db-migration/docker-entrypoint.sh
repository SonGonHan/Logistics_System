#!/bin/bash
set -e

echo "========== Запуск инициализации БД =========="

# Запустить приложение
java -jar app.jar

# Проверить статус
EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo "========== ✓ Миграции успешно завершены =========="
    exit 0
else
    echo "========== ✗ Миграции завершились с ошибкой (код $EXIT_CODE) =========="
    exit $EXIT_CODE
fi
