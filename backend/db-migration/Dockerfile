#FROM maven:3.9-eclipse-temurin-21-alpine AS builder
#WORKDIR /app
#COPY . .
#RUN mvn clean install -DskipTests
#
#FROM eclipse-temurin:21-jdk-alpine
#WORKDIR /app
#COPY --from=builder /app/backend/db-migration/target/db-migration-1.0.0.jar app.jar
#COPY --from=builder /app/backend/db-migration/docker-entrypoint.sh /app/entrypoint.sh
#RUN chmod +x /app/entrypoint.sh
#ENTRYPOINT ["/app/entrypoint.sh"]

# ============================================
# Stage 1: Build
# ============================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# ШАГ 1: Копируем КОРНЕВОЙ pom.xml
COPY pom.xml .

# ШАГ 2: Копируем pom.xml всех модулей (для dependency resolution)
COPY backend/shared-library/pom.xml backend/shared-library/
COPY backend/db-migration/pom.xml backend/db-migration/
COPY backend/user-auth-service/pom.xml backend/user-auth-service/

# ШАГ 3: Скачиваем зависимости (будет закэшировано)
RUN mvn dependency:go-offline -B -pl backend/db-migration -am

# ШАГ 4: Копируем исходники
COPY backend/shared-library backend/shared-library
COPY backend/db-migration backend/db-migration

# ШАГ 5: Собираем проект
RUN mvn clean package -pl backend/db-migration -am -DskipTests

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

LABEL service="db-migration"

WORKDIR /app

# Копируем JAR
COPY --from=builder /build/backend/db-migration/target/*.jar app.jar

# Копируем entrypoint
COPY backend/db-migration/docker-entrypoint.sh /app/entrypoint.sh

# Устанавливаем netcat для проверки PostgreSQL + права
RUN chmod +x /app/entrypoint.sh && \
    apk add --no-cache netcat-openbsd

ENTRYPOINT ["/app/entrypoint.sh"]
