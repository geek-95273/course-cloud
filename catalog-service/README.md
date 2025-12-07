# 校园选课与教学资源管理平台

## 项目介绍

本项目是一个基于Spring Boot的校园选课与教学资源管理平台，提供课程管理、学生管理和选课管理等核心功能。项目采用单体架构设计，使用内存存储，实现了完整的CRUD操作和RESTful API设计。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web**
- **Spring Validation**
- **Maven**

## 项目结构

```
src/
├── main/
│   ├── java/com/zjsu/course/
│   │   ├── CourseApplication.java          # 启动类
│   │   ├── common/                        # 通用类
│   │   │   └── ApiResponse.java           # 统一响应格式
│   │   ├── exception/                     # 异常处理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── BusinessException.java
│   │   ├── model/                         # 实体类
│   │   │   ├── Course.java
│   │   │   ├── Instructor.java
│   │   │   ├── ScheduleSlot.java
│   │   │   ├── Student.java
│   │   │   └── Enrollment.java
│   │   ├── repository/                    # 数据访问层
│   │   │   ├── CourseRepository.java
│   │   │   ├── StudentRepository.java
│   │   │   └── EnrollmentRepository.java
│   │   ├── service/                       # 业务逻辑层
│   │   │   ├── CourseService.java
│   │   │   ├── StudentService.java
│   │   │   └── EnrollmentService.java
│   │   └── controller/                    # 控制器层
│   │       ├── CourseController.java
│   │       ├── StudentController.java
│   │       └── EnrollmentController.java
│   └── resources/
│       └── application.yml                # 配置文件
└── test/
```

## 功能特性

### 1. 课程管理
- ✅ 创建课程（支持教师信息、时间安排、容量设置）
- ✅ 查询所有课程
- ✅ 根据ID查询单个课程
- ✅ 更新课程信息
- ✅ 删除课程

### 2. 学生管理
- ✅ 创建学生（自动生成UUID和创建时间）
- ✅ 查询所有学生
- ✅ 根据ID查询学生
- ✅ 更新学生信息
- ✅ 删除学生（检查选课记录）

### 3. 选课管理
- ✅ 学生选课
- ✅ 学生退课
- ✅ 查询所有选课记录
- ✅ 按课程查询选课记录
- ✅ 按学生查询选课记录

### 4. 业务规则
- ✅ 课程容量限制
- ✅ 重复选课检查
- ✅ 课程存在性验证
- ✅ 学生存在性验证
- ✅ 级联更新（选课人数自动增减）

## API 接口文档

### 课程管理 API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/courses` | 查询所有课程 |
| GET | `/api/courses/{id}` | 查询单个课程 |
| POST | `/api/courses` | 创建课程 |
| PUT | `/api/courses/{id}` | 更新课程 |
| DELETE | `/api/courses/{id}` | 删除课程 |

### 学生管理 API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/students` | 查询所有学生 |
| GET | `/api/students/{id}` | 查询单个学生 |
| POST | `/api/students` | 创建学生 |
| PUT | `/api/students/{id}` | 更新学生 |
| DELETE | `/api/students/{id}` | 删除学生 |

### 选课管理 API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/enrollments` | 查询所有选课记录 |
| GET | `/api/enrollments/course/{courseId}` | 按课程查询选课记录 |
| GET | `/api/enrollments/student/{studentId}` | 按学生查询选课记录 |
| POST | `/api/enrollments` | 学生选课 |
| DELETE | `/api/enrollments/{id}` | 学生退课 |

## 统一响应格式

所有API返回统一的JSON格式：

### 成功响应
```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

### 错误响应
```json
{
  "code": 404,
  "message": "Course not found",
  "data": null
}
```

## 如何运行项目

### 环境要求
- Java 17+
- Maven 3.6+

### 运行步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd course-management
   ```

2. **编译项目**
   ```bash
   mvn clean compile
   ```

3. **运行项目**
   ```bash
   mvn spring-boot:run
   ```

4. **访问应用**
   - 应用将在 `http://localhost:8080` 启动
   - API基础路径：`http://localhost:8080/api`

## 数据库（MySQL）运行说明

本项目当前仅支持通过 MySQL 持久化运行（production 模式）。文档聚焦于生产环境（MySQL）的部署与验证步骤。

1) 准备 MySQL 数据库

 - 创建数据库（示例）：
   ```sql
   CREATE DATABASE course_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2) 配置连接信息

 - 打开 `src/main/resources/application-prod.yml`，将 `spring.datasource.url`、`username`、`password` 修改为你的 MySQL 实例信息。

3) 导入表结构与测试数据（recommended）

 - 建议手动导入 SQL 脚本以确保表结构与实体一致：
   ```powershell
   mysql -u <user> -p course_db < src/main/resources/db/schema.sql
   mysql -u <user> -p course_db < src/main/resources/db/data.sql   # 可选测试数据
   ```

 - 生产环境中 `application-prod.yml` 使用 `ddl-auto: validate`，因此必须先创建表或使用迁移工具（Flyway/Liquibase）。

4) 启动应用（生产模式）

 - 使用 Maven 运行：
   ```powershell
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

 - 或先打包再运行：
   ```powershell
   mvn -DskipTests package
   java -jar target\course-management-1.0.0.jar --spring.profiles.active=prod
   ```

5) 验证数据库连通性

 - 健康检查：
   GET http://localhost:8080/health/db  应返回 "OK"

6) 运行 API 测试

 - 按 `test-api.http` 中的顺序或使用 Postman/Apifox 测试 CRUD 与选课流程。

注意：如果你需要在本地快速迭代并且无法搭建 MySQL，可以通过临时在 `application-prod.yml` 中将 `spring.jpa.hibernate.ddl-auto` 改为 `update` 来让应用自动创建表（仅用于开发/迁移阶段，不建议长期在生产中使用）。

### 使用IDE运行
1. 导入Maven项目到IDE
2. 找到 `CourseApplication.java` 启动类
3. 右键运行 `main` 方法

## 测试说明

### 使用Postman测试
1. 导入 `test-api.http` 文件到Postman
2. 确保应用已启动
3. 按顺序执行测试场景

### 使用curl测试
```bash
# 创建课程
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CS101",
    "title": "计算机科学导论",
    "instructor": {
      "id": "T001",
      "name": "张教授",
      "email": "zhang@example.edu.cn"
    },
    "schedule": {
      "dayOfWeek": "MONDAY",
      "startTime": "08:00",
      "endTime": "10:00",
      "expectedAttendance": 50
    },
    "capacity": 60
  }'

# 查询所有课程
curl -X GET http://localhost:8080/api/courses
```

## 测试场景

### 1. 完整的课程管理流程
- 创建3门不同的课程
- 查询所有课程，验证返回3条记录
- 根据ID查询某门课程
- 更新该课程的信息
- 删除该课程
- 再次查询，验证返回404

### 2. 选课业务流程
- 创建一门容量为2的课程
- 学生选课，验证成功
- 学生再次选课，验证失败（重复选课）
- 学生选课，验证失败（容量已满）
- 查询课程，验证enrolled字段

### 3. 学生管理流程
- 创建3个不同学号的学生
- 查询所有学生，验证返回3条记录
- 根据ID查询某个学生
- 更新该学生的信息
- 尝试删除有选课记录的学生，验证返回错误
- 删除没有选课记录的学生，验证删除成功

### 4. 错误处理
- 查询不存在的资源，验证返回404
- 创建资源时缺少必填字段，验证返回400
- 业务规则验证（容量限制、重复选课等）

## 数据模型

### Course（课程）
- `id`: 课程唯一标识符
- `code`: 课程代码
- `title`: 课程名称
- `instructor`: 教师信息
- `schedule`: 时间安排
- `capacity`: 课程容量
- `enrolled`: 已选人数

### Student（学生）
- `id`: 学生唯一标识符（UUID）
- `studentId`: 学号
- `name`: 姓名
- `major`: 专业
- `grade`: 入学年份
- `email`: 邮箱
- `createdAt`: 创建时间

### Enrollment（选课记录）
- `id`: 选课记录唯一标识符
- `courseId`: 课程ID
- `studentId`: 学生ID
- `enrolledAt`: 选课时间

## 开发规范

### 代码规范
- 遵循Java命名规范
- 使用Spring Boot最佳实践
- 合理的包结构和分层架构
- 完整的异常处理

### API设计规范
- RESTful API设计原则
- 统一的响应格式
- 正确的HTTP状态码使用
- 完整的参数验证

## 后续扩展

本项目为单体架构，后续可扩展为微服务架构：
- 课程服务
- 学生服务
- 选课服务
- 用户认证服务
- 网关服务

## 作者

- 包名：`com.zjsu.course`
- 项目：校园选课与教学资源管理平台
- 版本：1.0.0
