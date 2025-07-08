# 道威企业管理系统

一个基于Spring Boot的企业管理系统，专为样品制造企业设计，提供客户管理、样品管理、订单管理、员工管理等核心功能。

## 项目特色

- **现代化架构**: 基于Spring Boot 2.7.12 + MyBatis + MySQL
- **RESTful API**: 完整的REST API设计，支持前后端分离
- **文件上传**: 支持样品图片上传和管理
- **数据统计**: 提供丰富的Dashboard数据展示
- **事务管理**: 完善的数据库事务处理
- **统一响应**: 标准化的API响应格式

## 功能模块

### 客户管理
- 客户信息的增删改查
- 客户地址管理（支持多地址）
- 客户联系人管理（支持多联系人）
- 客户关联数据统计

### 样品管理
- 样品信息管理（型号、色号、库存、单价等）
- 样品图片上传和展示
- 样品与客户关联
- 样品库存管理

### 订单管理
- 订单创建和状态管理
- 订单与样品关联
- 订单状态跟踪（待生产、生产中、待发货、待收款、已完成）
- 订单金额和数量统计

### 员工管理
- 员工基本信息管理
- 员工通讯录
- 员工入职日期管理

### 数据看板
- 总待收款金额统计
- 近30天订单数量
- 客户和样品总数统计
- 订单状态分布图表
- 生产进度跟踪

### 用户认证
- 管理员登录系统
- 用户权限管理

## 技术栈

### 后端技术
- **框架**: Spring Boot 2.7.12
- **数据库**: MySQL 8.0
- **ORM**: MyBatis 2.3.1
- **JSON**: Jackson 2.13.5
- **工具**: Lombok
- **Java版本**: JDK 17

### 数据库设计
- **admin**: 管理员表
- **customer**: 客户信息表
- **customer_address**: 客户地址表
- **customer_contact**: 客户联系人表
- **sample**: 样品信息表
- **orders**: 订单表
- **employee**: 员工表

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-username/daowei_springboot.git
cd daowei_springboot
```

2. **数据库配置**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE daowei CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

# 导入数据表结构
mysql -u root -p daowei < mysql/all.sql
```

3. **修改配置文件**
编辑 `src/main/resources/application.properties`:
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/daowei
spring.datasource.username=your_username
spring.datasource.password=your_password

# 图片上传路径（请修改为你的实际路径）
upload.dir=C:/your/upload/path/
```

4. **编译运行**
```bash
# 使用Maven编译
mvn clean compile

# 运行项目
mvn spring-boot:run
```

5. **访问应用**
- 应用地址: http://localhost:8088
- API文档: http://localhost:8088/api/

## API接口

### 客户管理
- `GET /api/customers` - 获取客户列表
- `GET /api/customers/{id}` - 获取客户详情
- `POST /api/customers` - 创建客户
- `PUT /api/customers/{id}` - 更新客户
- `DELETE /api/customers/{id}` - 删除客户

### 样品管理
- `GET /api/samples` - 获取样品列表
- `GET /api/samples/{id}` - 获取样品详情
- `POST /api/samples` - 创建样品（支持图片上传）
- `PUT /api/samples/{id}` - 更新样品
- `DELETE /api/samples/{id}` - 删除样品

### 订单管理
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `PUT /api/orders/{id}` - 更新订单
- `DELETE /api/orders/{id}` - 删除订单

### 员工管理
- `GET /api/employees` - 获取员工列表
- `GET /api/employees/{id}` - 获取员工详情
- `POST /api/employees` - 添加员工
- `PUT /api/employees/{id}` - 更新员工
- `DELETE /api/employees/{id}` - 删除员工

### 数据看板
- `GET /api/dashboard` - 获取看板数据

### 用户认证
- `POST /api/admin/login` - 管理员登录
- `POST /api/user/login` - 用户登录

## 项目结构

```
src/main/java/com/example/demo/
├── Application.java                 # 启动类
├── common/
│   └── ApiResponse.java            # 统一响应格式
├── controller/                     # 控制器层
│   ├── AdminLoginController.java
│   ├── CustomerController.java
│   ├── DashboardController.java
│   ├── EmployeeController.java
│   ├── ImageController.java
│   ├── OrderController.java
│   ├── SampleController.java
│   └── UserLoginController.java
├── entity/                         # 实体类
│   ├── Admin.java
│   ├── Customer.java
│   ├── CustomerAddress.java
│   ├── CustomerContact.java
│   ├── Dashboard.java
│   ├── Employee.java
│   ├── Order.java
│   └── Sample.java
├── mapper/                         # 数据访问层
│   ├── CustomerMapper.java
│   ├── DashboardMapper.java
│   ├── EmployeeMapper.java
│   ├── OrderMapper.java
│   └── SampleMapper.java
└── service/                        # 业务逻辑层
    ├── impl/
    └── interfaces/
```

## 配置说明

### 数据库配置
- 数据库名: `daowei`
- 字符集: `utf8mb4`
- 端口: `3306`（默认）

### 文件上传配置
- 最大文件大小: 15MB
- 支持格式: 图片文件
- 存储路径: 可在配置文件中自定义

### 服务器配置
- 默认端口: 8088
- 可通过 `server.port` 修改

##  联系方式

- 项目创建维护者: [吕金鹏]
- 邮箱: 1813197353@qq.com
- 项目链接: https://github.com/LvKingRoc/daowei_springboot

