# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
# Asegúrate de que la estructura de directorios sea exactamente igual
COPY --from=builder /app/build/resources/main/ /app/resources/
EXPOSE ${PORT:-8080}
ENTRYPOINT ["java", "-Dspring.resources.static-locations=file:/app/resources/static/", "-Dspring.thymeleaf.prefix=file:/app/resources/templates/", "-jar", "app.jar"]
