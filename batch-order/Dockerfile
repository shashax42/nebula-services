FROM gradle:8.11-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle settings.gradle /app/
COPY src /app/src

RUN gradle build -x test

FROM eclipse-temurin:21-jre

COPY --from=builder /app/build/libs/batch-order.jar .

ENTRYPOINT ["java", "-jar", "batch-order.jar"]