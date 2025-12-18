# Calendar Design - 日历待办事项管理系统

一个功能完善的日历待办事项管理系统，支持月/周/日视图、任务管理、模板功能，并集成了KMS加密系统保护敏感数据。

## 📋 目录

- [项目简介](#项目简介)
- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [API文档](#api文档)
- [KMS加密系统](#kms加密系统)
- [开发指南](#开发指南)
- [部署说明](#部署说明)

---

## 项目简介

Calendar Design 是一个基于Spring Boot开发的日历待办事项管理系统，提供完整的任务管理功能，包括：

- 📅 多视图日历展示（月/周/日）
- ✅ 待办事项管理（创建、更新、完成、删除）
- 🏷️ 任务分类和标签
- 📝 任务模板功能
- 📊 数据统计和分析
- 🔐 KMS加密系统保护敏感数据
- 👤 用户认证和授权

## 核心功能

### 1. 日历视图

- **月视图**：显示整月的任务分布和完成情况
- **周视图**：显示一周内的详细任务列表
- **日视图**：显示单日的任务时间安排

### 2. 任务管理

- 创建、编辑、删除任务
- 任务状态管理（待办/已完成）
- 任务分类和类型管理
- 任务备注和附件支持
- 任务重复规则（每日/每周/每月）

### 3. 模板功能

- 创建任务模板
- 从模板快速创建任务
- 模板项管理

### 4. 数据统计

- 月度完成率统计
- 按类型统计任务分布
- 完成率趋势分析

### 5. 安全加密

- KMS加密系统保护敏感数据
- Todo标题和描述自动加密
- 用户手机号和邮箱自动加密
- 注解式加密，易于扩展

## 技术栈

### 后端框架

- **Spring Boot 3.5.3** - 应用框架
- **Spring Data JPA** - 数据持久化
- **Spring AOP** - 切面编程（加密功能）
- **H2 Database** - 嵌入式数据库（开发环境）

### 核心依赖

- **Lombok** - 简化Java代码
- **Hutool** - Java工具类库
- **Spring Security Crypto** - 密码加密
- **Jakarta Persistence** - JPA规范

### 加密技术

- **AES-256-GCM** - 对称加密算法
- **KMS** - 密钥管理服务
- **BCrypt** - 密码哈希

## 项目结构

```
calendar-design/
├── src/
│   ├── main/
│   │   ├── java/cn/barcke/
│   │   │   ├── annotation/          # 注解定义
│   │   │   │   └── EncryptField.java
│   │   │   ├── aspect/              # AOP切面
│   │   │   │   └── EncryptionAspect.java
│   │   │   ├── common/              # 通用类
│   │   │   │   ├── BarckeContext.java
│   │   │   │   ├── CommonException.java
│   │   │   │   ├── Result.java
│   │   │   │   └── ResultEnum.java
│   │   │   ├── config/              # 配置类
│   │   │   │   └── WebMvcConfig.java
│   │   │   ├── controller/          # 控制器层
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CalendarController.java
│   │   │   │   ├── TodoController.java
│   │   │   │   └── ...
│   │   │   ├── dao/                 # 数据访问层
│   │   │   │   ├── TodoRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── ...
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   │   ├── todo/
│   │   │   │   └── ...
│   │   │   ├── exception/           # 异常处理
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── interceptor/         # 拦截器
│   │   │   │   ├── AuthInterceptor.java
│   │   │   │   └── CORSSignatureInterceptor.java
│   │   │   ├── pojo/                # 实体类
│   │   │   │   ├── Todo.java
│   │   │   │   ├── UserInfo.java
│   │   │   │   ├── UserKey.java
│   │   │   │   └── ...
│   │   │   ├── service/             # 服务层
│   │   │   │   ├── impl/
│   │   │   │   │   ├── TodoServiceImpl.java
│   │   │   │   │   ├── KmsServiceImpl.java
│   │   │   │   │   └── ...
│   │   │   │   └── ...
│   │   │   └── tool/                # 工具类
│   │   │       ├── EncryptionUtil.java
│   │   │       └── JwtTool.java
│   │   └── resources/
│   │       ├── application.yml       # 配置文件
│   │       └── banner.txt
│   └── test/                         # 测试代码
├── doc/                              # 文档目录
│   └── KMS加密系统技术文档.md
├── pom.xml                           # Maven配置
└── README.md                         # 项目说明
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- H2 Database（已包含）

### 1. 克隆项目

```bash
git clone <repository-url>
cd calendar-design
```

### 2. 配置数据库

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:h2:file:/tmp/h2/testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
```

### 3. 配置KMS（可选）

编辑 `src/main/resources/application.yml`：

```yaml
kms:
  masterKey: YOUR_BASE64_ENCODED_32_BYTE_KEY
  enabled: true
```

### 4. 编译运行

```bash
# 编译
mvn clean package

# 运行
mvn spring-boot:run
```

### 5. 访问应用

- 应用地址：http://localhost:6158/api/
- H2控制台：http://localhost:6158/api/h2-console

## API文档

### 认证相关

#### 用户注册

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

#### 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "user-id",
      "username": "testuser",
      "nickname": null,
      "email": null,
      "phone": null
    }
  }
}
```

### 任务管理

#### 创建任务

```http
POST /api/todo
Authorization: BARCKE-TOKEN <token>
Content-Type: application/json

{
  "title": "完成项目文档",
  "description": "编写README和API文档",
  "date": "2025-12-20",
  "time": "14:00:00",
  "typeId": "type-id",
  "repeatType": "none"
}
```

#### 查询任务列表

```http
GET /api/todo?date=2025-12-20
Authorization: BARCKE-TOKEN <token>
```

#### 更新任务

```http
PUT /api/todo/{id}
Authorization: BARCKE-TOKEN <token>
Content-Type: application/json

{
  "title": "更新后的任务标题",
  "status": "completed"
}
```

#### 完成任务

```http
POST /api/todo/{id}/complete
Authorization: BARCKE-TOKEN <token>
```

#### 删除任务

```http
DELETE /api/todo/{id}
Authorization: BARCKE-TOKEN <token>
```

### 日历视图

#### 获取月视图

```http
GET /api/calendar/month?yearMonth=2025-12
Authorization: BARCKE-TOKEN <token>
```

#### 获取日视图

```http
GET /api/calendar/day?date=2025-12-20
Authorization: BARCKE-TOKEN <token>
```

#### 获取周视图

```http
GET /api/calendar/week?weekStartDate=2025-12-15
Authorization: BARCKE-TOKEN <token>
```

### 统计功能

#### 获取月度统计

```http
GET /api/statistics/month?yearMonth=2025-12
Authorization: BARCKE-TOKEN <token>
```

**响应**：
```json
{
  "code": 200,
  "data": {
    "month": "2025-12",
    "totalCount": 30,
    "completedCount": 20,
    "completionRate": 0.67
  }
}
```

## KMS加密系统

系统集成了完整的KMS加密系统，自动保护敏感数据。详细文档请参考：[KMS加密系统技术文档](./doc/KMS加密系统技术文档.md)

### 加密字段

- **Todo实体**：`title`、`description`
- **UserInfo实体**：`email`、`phone`

### 使用方式

在实体类字段上添加`@EncryptField`注解即可自动加密：

```java
@Entity
public class Todo {
    @EncryptField
    @Column(name = "title")
    private String title;
}
```

### 密钥管理

- 每个用户拥有独立的加密密钥
- 密钥使用主密钥加密后存储
- 支持密钥版本管理和轮换

## 开发指南

### 添加新功能

1. **创建实体类**：在`pojo`包下创建实体
2. **创建Repository**：在`dao`包下创建Repository接口
3. **创建Service**：在`service`包下创建Service接口和实现
4. **创建Controller**：在`controller`包下创建Controller
5. **创建DTO**：在`dto`包下创建请求/响应DTO

### 添加加密字段

在实体类字段上添加`@EncryptField`注解：

```java
@EncryptField
@Column(name = "sensitive_field")
private String sensitiveField;
```

### 代码规范

- 使用Lombok简化代码
- 遵循RESTful API设计规范
- 统一异常处理
- 使用统一的响应格式（Result类）

## 部署说明

### 生产环境配置

1. **数据库配置**：使用MySQL或PostgreSQL替代H2
2. **KMS主密钥**：使用环境变量或密钥管理服务
3. **JWT密钥**：使用强随机密钥
4. **日志配置**：配置日志级别和输出

### Docker部署（示例）

```dockerfile
FROM openjdk:21-jre-slim
COPY target/agile-dev.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 环境变量

```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/calendar
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# KMS配置
KMS_MASTER_KEY=your_base64_encoded_key
KMS_ENABLED=true

# JWT配置
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400
```

## 项目特性

### 安全性

- ✅ JWT认证机制
- ✅ 密码BCrypt加密
- ✅ KMS数据加密
- ✅ 用户级密钥隔离
- ✅ 敏感数据自动加密

### 性能优化

- ✅ 加密字段缓存
- ✅ 数据库索引优化
- ✅ 查询性能优化

### 可扩展性

- ✅ 注解式加密扩展
- ✅ 模块化设计
- ✅ 接口抽象

## 版本历史

### v1.0 (2025/12/16)

- ✅ 基础功能实现
- ✅ 日历视图（月/周/日）
- ✅ 任务管理
- ✅ 模板功能
- ✅ 统计功能
- ✅ KMS加密系统
- ✅ 用户认证

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

本项目采用 MIT 许可证。

## 联系方式

- 作者：Barcke

## 致谢

感谢所有为这个项目做出贡献的开发者！

---

**注意**：本项目仅供学习和参考使用，生产环境请根据实际需求进行安全加固和性能优化。

