#!/bin/sh
set -e

echo "=========================================="
echo "Starting Database Migration"
echo "=========================================="

# Ждем PostgreSQL
echo "Waiting for PostgreSQL..."
until nc -z postgres 5432 2>/dev/null; do
  echo "PostgreSQL unavailable - sleeping"
  sleep 2
done

echo "PostgreSQL is ready!"

# Запускаем миграцию
echo "Running migrations..."
java -jar app.jar

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
  echo "Migration completed successfully!"
  exit 0
else
  echo "Migration FAILED with exit code: $EXIT_CODE"
  exit $EXIT_CODE
fi
