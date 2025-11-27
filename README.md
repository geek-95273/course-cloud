# course-cloud (Microservices)

项目已按作业 `hw06` 要求拆分为两个微服务：

- `catalog-service`（课程目录服务） - 端口 `8081`，连接 `catalog_db` 数据库
- `enrollment-service`（选课服务） - 端口 `8082`，连接 `enrollment_db` 数据库，并通过 HTTP 调用 `catalog-service` 验证课程

版本: 1.0.0

快速运行（使用 Docker Compose）:

```bash
# 在项目根目录
docker-compose up -d --build
# 等待服务启动后运行测试脚本
./test-services.sh
```

主要文件:

- `docker-compose.yml` - 编排两个 MySQL 实例与两个服务
- `catalog-service/` - 课程服务源码与 Dockerfile
- `enrollment-service/` - 选课服务源码与 Dockerfile
- `test-services.sh` - 功能测试脚本
- `VERSION` - 版本号

API 参考:
- 课程服务（catalog-service）
  - GET `/api/courses`
  - GET `/api/courses/{id}`
  - GET `/api/courses/code/{code}`
  - POST `/api/courses`
  - PUT `/api/courses/{id}` (支持部分更新, 可用于更新 `enrolled` 值)
  - DELETE `/api/courses/{id}`

- 选课服务（enrollment-service）
  - GET `/api/students`
  - GET `/api/students/{id}`
  - POST `/api/students`
  - PUT `/api/students/{id}`
  - DELETE `/api/students/{id}`
  - GET `/api/enrollments`
  - GET `/api/enrollments/course/{courseId}`
  - GET `/api/enrollments/student/{studentId}`
  - POST `/api/enrollments`
  - DELETE `/api/enrollments/{id}`

更多细节请参阅各服务下的 `README.md`。

**运行与部署（详细）**

- 前置条件：`Docker`、`docker-compose`、`jq`（用于测试脚本中的 JSON 解析）；如果在本机不使用 Docker，请安装 `JDK 17+` 与 `Maven`。

- 构建镜像（若希望手动构建）：

```bash
# 在根目录先打包两个服务
cd catalog-service && mvn -DskipTests package
cd ../enrollment-service && mvn -DskipTests package

# 回到项目根目录，构建并启动容器
docker-compose up -d --build
```

- 直接一键启动（推荐）：

```bash
docker-compose up -d --build
```

- 查看日志：

```bash
docker-compose logs -f catalog-service
docker-compose logs -f enrollment-service
```

- 健康检查：

  - 数据库：`catalog-db` 在宿主机映射端口 `3307`，`enrollment-db` 映射端口 `3308`。
  - 应用：
    - catalog-service: http://localhost:8081/health/db
    - enrollment-service: http://localhost:8082/actuator/health （若启用 actuator profile）

**测试脚本（`test-services.sh`）**

- 使脚本可执行：

```bash
chmod +x test-services.sh
./test-services.sh
```

- 该脚本会按顺序：创建课程、查询课程、创建学生、查询学生、选课并查询选课记录、测试课程不存在时的错误响应。脚本使用 `jq` 美化输出。

**本地开发（不使用 Docker）**

- 启动 catalog-service（开发 profile/H2）:

```bash
cd catalog-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

- 启动 enrollment-service（开发 profile/H2）:

```bash
cd enrollment-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**环境变量与配置说明（Docker 环境）**

- `docker-compose.yml` 中把数据库连接与凭据通过环境变量传入服务：`DB_URL`、`DB_USERNAME`、`DB_PASSWORD`。
- `enrollment-service` 还通过 `catalog-service.url` 环境变量指定课程目录服务地址（容器内名称 `http://catalog-service:8081`）。

**如何提交（建议）**

- 版本标签：

```bash
git add .
git commit -m "feat: split into catalog-service and enrollment-service, add docker-compose and tests"
git tag -a v1.2.0 -m "Release version 1.2.0 - Microservices architecture"
git push origin main --follow-tags
```

**常见问题与排查**

- 如果 `enrollment-service` 报 `Course not found`：确认 `catalog-service` 已启动并能在容器网络中访问 `http://catalog-service:8081/api/courses`。
- 如果数据库容器未初始化：检查 `docker-compose logs catalog-db` 并在必要时执行 `docker-compose down -v` 后重建。

如果你希望，我可以：
- 帮你在本机启动 `docker-compose` 并运行 `test-services.sh`（需要你允许在环境中运行 Docker 命令），或者
- 为 `catalog-service` 添加 Swagger/OpenAPI 文档，或
- 添加更多集成测试。
