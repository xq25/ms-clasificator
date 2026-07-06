# ─────────────────────────────────────────────
# Stage 1: build
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Primero solo archivos de dependencias para aprovechar caché de Docker.
# Si el código cambia pero pom.xml no, esta capa no se reconstruye.
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Código fuente y compilación
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ─────────────────────────────────────────────
# Stage 2: runtime  (imagen final, sin JDK)
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# wget lo usa el healthcheck del compose
RUN apk add --no-cache wget

# Usuario sin privilegios
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/ms-clasificator-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8081

# Las siguientes propiedades se inyectan como variables de entorno en docker-compose.
# Spring Boot las resuelve automáticamente (guión bajo → punto, mayúsculas → minúsculas):
#
#   MS_SECURITY_URL              → ms.security.url
#   APP_CORS_ALLOWED_ORIGINS     → app.cors.allowed-origins
#
#   SPRING_DATASOURCE_URL        → spring.datasource.url
#   SPRING_DATASOURCE_USERNAME   → spring.datasource.username
#   SPRING_DATASOURCE_PASSWORD   → spring.datasource.password
#
#   STORAGE_MINIO_ENDPOINT       → storage.minio.endpoint
#   STORAGE_MINIO_ACCESS_KEY     → storage.minio.access-key
#   STORAGE_MINIO_SECRET_KEY     → storage.minio.secret-key
#   STORAGE_MINIO_BUCKET         → storage.minio.bucket
#
#   SPRING_PROFILES_ACTIVE       → spring.profiles.active  (mantener "dev" para MinIO)
# "dev" mantiene activos los @Profile("dev") de StorageConfig (MinIO).
# "docker" activa application-docker.properties con las URLs del compose.
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=dev,docker", \
  "-jar", "app.jar"]
