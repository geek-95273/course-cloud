# Multi-stage build for user-service
FROM maven:3.9.6-eclipse-temurin-8 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:8-jre
WORKDIR /app
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app/app.jar"]
