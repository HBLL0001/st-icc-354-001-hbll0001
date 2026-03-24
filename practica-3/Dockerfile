# ============================================
# Multi-stage Dockerfile – Mockup API Server
# ============================================

# ── Stage 1: Build ──────────────────────────
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy Gradle wrapper and config first (layer cache for dependencies)
COPY app/build.gradle app/settings.gradle ./
COPY app/gradlew ./
COPY app/gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY app/src ./src

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
