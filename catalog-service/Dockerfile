# Runtime Dockerfile for catalog-service
FROM eclipse-temurin:25-jre
WORKDIR /app
EXPOSE 8081
COPY target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
