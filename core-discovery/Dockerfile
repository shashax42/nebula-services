FROM gradle:8.10-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle settings.gradle /app/
COPY src /app/src

RUN gradle build -x test

FROM openjdk:21-slim

COPY --from=builder /app/build/libs/core-discovery.jar .

ENTRYPOINT ["java", "-jar", "core-discovery.jar"]
EXPOSE 8761