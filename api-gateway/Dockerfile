# ── Stage 1: Build ─────────────────────────────────────────────────────────────
# Use gradle image directly — no ./gradlew needed (avoids CRLF issues on Windows hosts)
FROM gradle:8.9-jdk21-alpine AS builder

WORKDIR /app
# Copy only dependency files first for layer caching
COPY settings.gradle build.gradle ./
COPY gradle/ gradle/

# Cache dependencies
RUN gradle dependencies --no-daemon -q || true

# Copy source and build
COPY src/ src/
RUN gradle bootJar --no-daemon -q

# ── Stage 2: Runtime ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
