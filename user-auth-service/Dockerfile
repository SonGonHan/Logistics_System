FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM maven:3.9-eclipse-temurin-21-alpine
WORKDIR /app
COPY --from=builder /app/user-auth-service/target/user-auth-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
