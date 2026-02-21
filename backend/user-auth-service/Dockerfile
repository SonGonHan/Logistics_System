#FROM maven:3.9-eclipse-temurin-21-alpine AS builder
#WORKDIR /app
#COPY . .
#RUN mvn clean install -DskipTests
#
#FROM maven:3.9-eclipse-temurin-21-alpine
#WORKDIR /app
#COPY --from=builder /app/user-auth-service/target/user-auth-service-1.0.0.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]



# ============================================
# Stage 1: Build
# ============================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# ШАГ 1: Копируем КОРНЕВОЙ pom.xml
COPY pom.xml .

# ШАГ 2: Копируем pom.xml всех модулей
COPY backend/shared-library/pom.xml backend/shared-library/
COPY backend/db-migration/pom.xml backend/db-migration/
COPY backend/user-auth-service/pom.xml backend/user-auth-service/
COPY backend/core-business-service/pom.xml backend/core-business-service/

# ШАГ 3: Скачиваем зависимости (кэшируется)
RUN mvn dependency:go-offline -B -pl backend/user-auth-service -am

# ШАГ 4: Копируем исходники
COPY backend/shared-library backend/shared-library
COPY backend/user-auth-service backend/user-auth-service

# ШАГ 5: Собираем проект
RUN mvn clean package -pl backend/user-auth-service -am -DskipTests

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

LABEL service="user-auth-service"

WORKDIR /app

# Создаем непривилегированного пользователя
RUN addgroup -S spring && adduser -S spring -G spring

# Копируем JAR
COPY --from=builder /build/backend/user-auth-service/target/*.jar app.jar

# Создаем папку логов + устанавливаем wget для healthcheck
RUN chown spring:spring app.jar && \
    mkdir -p /logs && \
    chown spring:spring /logs && \
    apk add --no-cache wget

USER spring:spring

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
