# ============================================
# Multi-stage Dockerfile – Mockup API Server
# ============================================

# ── Stage 1: Build ──────────────────────────
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy Gradle wrapper and config first (cache dependencies)
COPY ../practica-1/build.gradle ../practica-1/settings.gradle ./
COPY ../practica-1/gradlew ./
COPY ../practica-1/gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY ../practica-1/src ./src

# Build the fat JAR (skip tests for faster build)
RUN gradle bootJar --no-daemon

# ── Stage 2: Runtime ───────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (parameterized at runtime)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
