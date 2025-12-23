# 道威管理系统 - 后端

> 基于 Spring Boot 的企业管理系统后端，提供 RESTful API 和 SSE 实时推送

## 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Spring Boot 2.7.12 |
| 数据库 | PostgreSQL (默认) / MySQL |
| ORM | MyBatis 2.3.1 |
| 认证 | JWT |
| 实时推送 | SSE (Server-Sent Events) |
| JSON | Jackson |
| Java | JDK 8+ |

## 项目结构

```
src/main/java/com/example/demo/
├── Application.java                  # 启动类
├── common/
│   └── ApiResponse.java              # 统一响应格式
├── controller/                       # 控制器层
│   ├── NotificationController.java   # SSE实时推送
│   ├── OrderController.java          # 订单管理
│   ├── SampleController.java         # 样品管理
│   ├── CustomerController.java       # 客户管理
│   ├── EmployeeController.java       # 员工管理
│   ├── DashboardController.java      # 数据看板
│   ├── AuthController.java           # 认证/心跳
│   ├── AdminLoginController.java     # 管理员登录
│   ├── UserLoginController.java      # 用户登录
│   ├── ImageController.java          # 图片服务
│   ├── OperationLogController.java   # 操作日志
│   └── ProxyController.java          # 第三方API代理
├── entity/                           # 实体类
│   ├── Sample.java, Order.java
│   ├── Customer.java, CustomerAddress.java, CustomerContact.java
│   ├── Employee.java, Admin.java, User.java
│   └── OperationLog.java
├── mapper/                           # MyBatis Mapper
├── service/                          # 业务逻辑层
│   └── impl/
├── dto/                              # 数据传输对象
├── annotation/
│   └── Log.java                      # 操作日志注解
├── aspect/
│   └── LogAspect.java                # AOP日志切面
└── config/                           # 配置类
    └── WebConfig.java                # CORS配置
```

## 功能模块

### 业务接口

| 模块 | 接口前缀 | 功能 |
|------|----------|------|
| 样品管理 | `/api/samples` | CRUD + 图片上传 + 实时同步 |
| 订单管理 | `/api/orders` | CRUD + 状态流转 + 实时同步 |
| 客户管理 | `/api/customers` | CRUD + 多地址/联系人 |
| 员工管理 | `/api/employees` | CRUD |
| 用户管理 | `/api/users` | CRUD |
| 数据看板 | `/api/dashboard` | 统计数据 |
| 操作日志 | `/api/logs` | 查询操作记录 |

### 系统接口

| 模块 | 接口前缀 | 功能 |
|------|----------|------|
| SSE推送 | `/api/notifications` | 实时数据同步 |
| 认证 | `/api/auth` | 心跳检测 |
| 登录 | `/api/admin/login`, `/api/user/login` | 用户认证 |
| 图片 | `/sampleImage/**` | 图片访问 |
| 代理 | `/api/proxy/**` | 第三方API代理(天气/日历/OCR) |

### 特色功能

- **SSE实时推送** - 订单/样品更新时广播到所有客户端
- **操作日志** - 使用 `@Log` 注解自动记录增删改操作
- **JWT认证** - Token验证 + 心跳检测
- **图片处理** - 上传、自动生成缩略图
- **第三方代理** - 天气/日历/OCR API代理（隐藏密钥）

## 快速开始

### 环境要求
- JDK 8+
- PostgreSQL 14+ (或 MySQL 8+)
- Maven 3.6+

### 配置文件 (application.properties)
```properties
# 服务端口
server.port=661

# PostgreSQL 数据库
spring.datasource.url=jdbc:postgresql://localhost:5432/daowei
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# 图片上传路径
upload.dir=/path/to/uploads/
sample.image.dir=/path/to/uploads/sampleImage/

# JWT配置
jwt.secret=your_secret_key
jwt.expiration=86400000

# 文件上传限制
spring.servlet.multipart.max-file-size=15MB
spring.servlet.multipart.max-request-size=15MB
```

### 配置步骤
```bash
# 1. 复制配置模板
cp src/main/resources/application.properties.example src/main/resources/application.properties

# 2. 编辑配置文件，填写真实的数据库密码、API密钥等
# 3. 运行
mvn clean compile
mvn spring-boot:run
```

> ⚠️ **注意**: `application.properties` 包含敏感信息，已加入 `.gitignore`，不会被提交到git

## API 接口详情

### SSE实时推送
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/notifications/subscribe?userId=xxx` | 订阅SSE |
| GET | `/api/notifications/connections` | 获取连接数 |
| POST | `/api/notifications/test` | 测试广播 |

### 订单管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/orders` | 获取列表 |
| GET | `/api/orders/{id}` | 获取详情 |
| POST | `/api/orders` | 创建 |
| PUT | `/api/orders/{id}` | 更新 (触发SSE) |
| DELETE | `/api/orders/{id}` | 删除 |

### 样品管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/samples` | 获取列表 |
| GET | `/api/samples/{id}` | 获取详情 |
| POST | `/api/samples` | 创建 (multipart) |
| PUT | `/api/samples/{id}` | 更新 (触发SSE) |
| DELETE | `/api/samples/{id}` | 删除 |
| DELETE | `/api/samples/{id}/image` | 删除图片 |

### 客户管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/customers` | 获取列表 |
| GET | `/api/customers/{id}` | 获取详情(含地址/联系人) |
| POST | `/api/customers` | 创建 |
| PUT | `/api/customers/{id}` | 更新 |
| DELETE | `/api/customers/{id}` | 删除 |

## SSE 实时推送

### 工作流程
```
前端连接 → 后端SseEmitter → 数据变更 → broadcast() → 前端更新
```

### 事件类型
| 事件名 | 说明 |
|--------|------|
| `connect` | 连接成功 |
| `order_sync` | 订单同步 |
| `sample_sync` | 样品同步 |

### 消息格式
```json
{
  "action": "update",
  "order": { "id": 1, "orderNumber": "...", ... }
}
```

## 数据库表

| 表名 | 说明 |
|------|------|
| admin | 管理员 |
| users | 普通用户 |
| customer | 客户 |
| customer_address | 客户地址 |
| customer_contact | 客户联系人 |
| sample | 样品 |
| orders | 订单 |
| employee | 员工 |
| operation_log | 操作日志 |

## 联系方式

- **维护者**: 慕容雪歌
- **邮箱**: 1813197353@qq.com

