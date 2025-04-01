# config-server/Dockerfile
FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test


FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl

# GitHub Credentials
ARG GIT_USERNAME
ARG GIT_PW
ARG RABBITMQ_HOST
ARG RABBITMQ_PORT
ARG RABBITMQ_USER
ARG RABBITMQ_PASSWORD

ENV GIT_USERNAME=${GIT_USERNAME}
ENV GIT_PW=${GIT_PW}
ENV RABBITMQ_HOST=${RABBITMQ_HOST}
ENV RABBITMQ_PORT=${RABBITMQ_PORT}
ENV RABBITMQ_USER=${RABBITMQ_USER}
ENV RABBITMQ_PASSWORD=${RABBITMQ_PASSWORD}

VOLUME /tmp
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8071
ENTRYPOINT ["java", "-jar", "/app.jar"]