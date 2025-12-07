# Root Dockerfile to build all modules and export their JARs (not used to run services)
FROM maven:3.8.7-eclipse-temurin-17 AS builder

# 先检查文件是否存在，如果不存在则创建
RUN if [ ! -f /usr/share/maven/conf/settings.xml ]; then \
    echo "原始 settings.xml 不存在，创建默认配置..." && \
    mkdir -p /usr/share/maven/conf/; \
    fi

# 创建自定义的 Maven 配置
RUN echo '<?xml version="1.0" encoding="UTF-8"?> \
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 \
          http://maven.apache.org/xsd/settings-1.0.0.xsd"> \
    <mirrors> \
        <mirror> \
            <id>aliyunmaven</id> \
            <mirrorOf>*</mirrorOf> \
            <name>阿里云公共仓库</name> \
            <url>https://maven.aliyun.com/repository/public</url> \
        </mirror> \
    </mirrors> \
</settings>' > /usr/share/maven/conf/settings.xml

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
COPY --from=builder /workspace/catalog-service/target/*.jar /dist/catalog-service.jar
COPY --from=builder /workspace/enrollment-service/target/*.jar /dist/enrollment-service.jar
COPY --from=builder /workspace/user-service/target/*.jar /dist/user-service.jar
CMD ["sh", "-c", "ls -l /dist && echo 'Artifacts ready under /dist'"]