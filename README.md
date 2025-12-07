# course-cloud 微服务选课系统

## 项目简介
- 三个 Spring Boot 微服务：课程目录（catalog-service）、选课（enrollment-service）、用户/学生（user-service），每个服务对应独立 MySQL。
- 业务规则：只有 enrollment-service 通过 HTTP 调用 catalog-service 与 user-service 校验课程/学生与更新已选人数，其他服务不直接互调。
- 支持 dev（内存库）与 prod（MySQL）配置，本文档聚焦 Docker 化 prod 部署，并集成 Nacos 做服务注册/发现。

## 架构图
```
                +------------------+
                | catalog-service  |----> catalog-db (MySQL)
                +------------------+
                         ^
                         |
 +------------------+    |
 | enrollment       |----+
 | -service         |-----------+
 +------------------+           |
        ^                       |
        |                       v
        |              +----------------+
        |              | user-service   |----> user-db (MySQL)
        |              +----------------+
        |
   调用 catalog/user 校验与更新
```

## 技术栈
- Java 8, Spring Boot 2.7.x, Spring Data JPA, Validation
- MySQL 8.0（按服务一库），H2 用于 dev
- Docker / Docker Compose，基于 eclipse-temurin JRE 与 maven 镜像

## 环境要求
- Docker 20+ 与 Docker Compose v2
- (可选) Maven 3.9+、JDK 8+ 若需本地构建或运行 `mvn`/单体测试
- 端口占用：8080(user)、8081(catalog)、8082(enrollment)、3307/3308/3309 对应三个 MySQL

## 构建和运行步骤
- 一键启动（推荐）  
  `docker compose up -d --build`
- 验证：访问 `http://localhost:8080/api/students`、`http://localhost:8081/api/courses`、`http://localhost:8082/api/enrollments`
- 停止并清理：`docker compose down -v`
- 本地手动构建（可选）  
  `mvn -pl catalog-service,enrollment-service,user-service -am clean package -DskipTests`
- 环境变量（compose 已设置默认值，可根据需要覆盖）  
  - `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`：各服务数据库连接  
  - `USER_SERVICE_URL`、`CATALOG_SERVICE_URL`：enrollment-service 外部调用地址  
  - `SPRING_PROFILES_ACTIVE=prod`：启用 MySQL
  - `NACOS_SERVER_ADDR`、`NACOS_NAMESPACE`、`NACOS_GROUP`：Nacos 注册中心地址/命名空间/分组（默认 `nacos:8848`、`dev`、`COURSEHUB_GROUP`）

## Nacos 部署与配置
- Compose 已内置 Nacos（standalone），端口 `8848`(HTTP) / `9848`(gRPC)。默认账号密码：`nacos / nacos`，控制台：`http://localhost:8848/nacos`
- 各服务在 `application.yml` 配置了 Nacos discovery：服务名（`catalog-service`、`user-service`、`enrollment-service`）、命名空间 `dev`、分组 `COURSEHUB_GROUP`、心跳间隔/超时。
- 健康检查：使用 Spring Boot Actuator `/actuator/health`，Nacos 将根据实例心跳与健康状态标记实例是否可用。
- 服务间调用：enrollment-service 通过 `@LoadBalanced RestTemplate` + Nacos 服务名发起调用，不再依赖硬编码地址。
- 多实例验证：可在服务器上多次 `docker compose up --scale enrollment-service=2`，然后调用 `http://<host>:8082/api/enrollments/test` 观察返回的 `port` 字段轮换，以验证负载均衡/故障转移；停止一个实例后请求仍应成功。

## API 文档（按服务）
- catalog-service（端口 8081）  
  - `GET /api/courses` 列表  
  - `GET /api/courses/{id}` 按 ID 查询  
  - `GET /api/courses/code/{code}` 按课程代码查询  
  - `POST /api/courses` 创建课程  
  - `PUT /api/courses/{id}` 更新（接受部分字段 Map）  
  - `DELETE /api/courses/{id}` 删除
- user-service（端口 8080）  
  - `POST /api/students` 创建学生  
  - `GET /api/students` 列表  
  - `GET /api/students/{id}` 按 ID 查询  
  - `PUT /api/students/{id}` 更新  
  - `DELETE /api/students/{id}` 删除
- enrollment-service（端口 8082）  
  - `POST /api/enrollments` 学生选课（调用 user/catalog 校验）  
  - `DELETE /api/enrollments/{id}` 退课  
  - `GET /api/enrollments` 列表  
  - `GET /api/enrollments/course/{courseId}` 按课程查询  
  - `GET /api/enrollments/student/{studentId}` 按学生查询
  - `GET /api/enrollments/test` 返回当前实例端口（用于负载均衡/故障转移验证）

## 测试说明
- 现有项目未提供自动化测试；可在根目录运行 `mvn test` 针对各模块。容器镜像构建时默认 `-DskipTests`，避免拉长构建时间。
- Nacos 联调脚本：`scripts/nacos-test.sh`（需要本机已安装 Docker/Compose）。
- 建议为跨服务调用增加集成测试（如使用 Testcontainers + WireMock 模拟 catalog/user 服务），以覆盖选课校验与并发选课场景。

## 遇到的问题和解决方案
- **容器内数据库初始化**：MySQL 官方镜像不会自动执行 Spring 的 `schema.sql`/`data.sql`（非嵌入库时默认禁用）。通过 docker-compose 将各服务的 schema/data.sql 挂载到 `/docker-entrypoint-initdb.d`，保证初始表结构与示例数据。
- **服务间地址**：原配置默认 `localhost`，容器内无法互访。已改为支持环境变量并默认使用容器服务名（如 `http://catalog-service:8081`），同时在 compose 中注入。
- **JDK 版本不一致**：原 Dockerfile 使用高版本 JRE，已统一到 Java 8（与代码/依赖匹配）并改为多阶段构建，镜像尺寸更小且无需预先构建 JAR。
- **通信方向约束**：仅 enrollment-service 依赖 catalog/user 服务，其他服务不发起互调；compose 拓扑与配置已保持这一方向。
- **服务注册与发现**：引入 Nacos（standalone）并在 `application.yml` 配置 `spring.cloud.nacos.discovery.*`，enrollment-service 使用 `@LoadBalanced RestTemplate` + 服务名调用，实现多实例负载均衡与故障转移。
