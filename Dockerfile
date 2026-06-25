# ─────────────────────────────────────────────
# Stage 1: build
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiamos solo el pom primero para cachear la descarga de dependencias.
# Si el pom no cambia, Docker reutiliza esta capa en rebuilds.
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ─────────────────────────────────────────────
# Stage 2: runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/ms-clasificator-0.0.1-SNAPSHOT.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

# Puerto declarado (no expuesto al host — lo maneja el compose)
EXPOSE 8081

# JVM tuning mínimo para contenedores:
# -XX:+UseContainerSupport  → respeta los límites de memoria del cgroup
# -XX:MaxRAMPercentage=75.0 → usa máximo 75% de la RAM asignada al contenedor
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
