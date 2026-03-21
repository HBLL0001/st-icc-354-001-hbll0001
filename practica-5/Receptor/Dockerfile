# Build stage
FROM amazoncorretto:17-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Run stage
FROM amazoncorretto:17-alpine
LABEL authors="Armando"
COPY --from=builder /app/build/libs/Receptor-0.0.1-SNAPSHOT.jar receptor.jar

ENTRYPOINT ["java", "-jar", "/receptor.jar"]