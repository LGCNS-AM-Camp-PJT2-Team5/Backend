# gateway/Dockerfile
FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon  # 테스트 생략

FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8072
ENTRYPOINT ["java", "-jar", "/app.jar"]
