# gateway/Dockerfile
FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon  # 테스트 생략

FROM openjdk:21-jdk-slim

ARG RABBITMQ_HOST
ARG RABBITMQ_PORT
ARG RABBITMQ_USER
ARG RABBITMQ_PASSWORD

ENV RABBITMQ_HOST=${RABBITMQ_HOST}
ENV RABBITMQ_PORT=${RABBITMQ_PORT}
ENV RABBITMQ_USER=${RABBITMQ_USER}
ENV RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD}

VOLUME /tmp
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8072
ENTRYPOINT ["java", "-jar", "/app.jar"]
