# Multi-stage build for catalog-service
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

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS} -jar /app/app.jar"]