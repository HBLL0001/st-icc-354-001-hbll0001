# Stage 1
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --info
RUN ls -la build/libs/

# Stage 2
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

VOLUME /app/logs

COPY --from=build /workspace/app/build/libs/PracticaSpringboot-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "-Dserver.port=${SERVER_PORT}", "app.jar"]