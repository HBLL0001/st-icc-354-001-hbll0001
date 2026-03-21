# Build stage
FROM amazoncorretto:17-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Run stage
FROM amazoncorretto:17-alpine
LABEL authors="Armando"
COPY --from=builder /app/build/libs/Cliente2-0.0.1-SNAPSHOT.jar cliente2.jar

ENTRYPOINT ["java", "-jar", "/cliente2.jar"]