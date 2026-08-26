# ── Stage 1: Build ─────────────────────────────────────────────────────────────
FROM gradle:8.9-jdk21-alpine AS builder

WORKDIR /app
# Copy gradle wrapper and dependency files first (layer cache)
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/
RUN chmod +x gradlew

# Download dependencies in a separate cached layer
RUN ./gradlew dependencies --no-daemon -q || true

# Copy source and build
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -q

# ── Stage 2: Runtime ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
