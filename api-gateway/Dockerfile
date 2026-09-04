# ── Stage 1: Build ─────────────────────────────────────────────────────────────
FROM gradle:8.9-jdk21-alpine AS builder

WORKDIR /app
COPY settings.gradle build.gradle ./
COPY gradle/ gradle/

RUN gradle dependencies --no-daemon -q || true

COPY src/ src/
RUN gradle bootJar --no-daemon -x test --parallel

# ── Stage 2: Runtime ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
