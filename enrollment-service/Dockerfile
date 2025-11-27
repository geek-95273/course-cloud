# Runtime Dockerfile for enrollment-service
FROM eclipse-temurin:25-jre
WORKDIR /app
EXPOSE 8082
COPY target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
