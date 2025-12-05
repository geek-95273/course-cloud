# Root Dockerfile to build all modules and export their JARs (not used to run services)
FROM maven:3.9.6-eclipse-temurin-8 AS builder
WORKDIR /workspace

# Cache dependencies
COPY pom.xml .
COPY catalog-service/pom.xml catalog-service/pom.xml
COPY enrollment-service/pom.xml enrollment-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
RUN mvn -B -pl catalog-service,enrollment-service,user-service -am dependency:go-offline

# Build all modules
COPY catalog-service catalog-service
COPY enrollment-service enrollment-service
COPY user-service user-service
RUN mvn -B -pl catalog-service,enrollment-service,user-service -am clean package -DskipTests

# Collect artifacts
FROM alpine:3.19
WORKDIR /dist
COPY --from=builder /workspace/catalog-service/target/course-management-*.jar /dist/catalog-service.jar
COPY --from=builder /workspace/enrollment-service/target/course-management-*.jar /dist/enrollment-service.jar
COPY --from=builder /workspace/user-service/target/course-management-*.jar /dist/user-service.jar
CMD ["sh", "-c", "ls -l /dist && echo 'Artifacts ready under /dist'"]
