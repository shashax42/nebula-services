FROM gradle:8.10-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle settings.gradle /app/
COPY src /app/src

RUN gradle build -x test

FROM openjdk:21-slim

COPY --from=builder /app/build/libs/core-gateway.jar .

ENTRYPOINT ["java", "-jar", "core-gateway.jar"]
EXPOSE 8080
EXPOSE 80