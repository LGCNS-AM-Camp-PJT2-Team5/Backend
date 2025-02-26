# jobbotdari/Dockerfile
FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test

FROM openjdk:21-jdk-slim

# MySQL 환경 변수
ARG MYSQL_DATABASE
ARG MYSQL_USER
ARG MYSQL_PASSWORD

ENV SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/${MYSQL_DATABASE}?serverTimezone=UTC"
ENV SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
ENV SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}

VOLUME /tmp
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]