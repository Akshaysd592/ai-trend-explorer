# ── Stage 1: Build ─────────────────────────────────────────────────────────────
# trend-service uses openApiGenerate task — must run before bootJar
FROM gradle:8.9-jdk21-alpine AS builder

WORKDIR /app
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/
RUN chmod +x gradlew

# Cache dependencies
RUN ./gradlew dependencies --no-daemon -q || true

COPY src/ src/
# openApiGenerate runs automatically via compileJava.dependsOn
RUN ./gradlew bootJar --no-daemon -q

# ── Stage 2: Runtime ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
