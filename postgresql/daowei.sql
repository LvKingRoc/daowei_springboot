/*
 道威系统 - PostgreSQL 数据库初始化脚本
 
 数据库名称: daowei
 字符集: UTF-8
 
 包含表:
 - admin: 管理员表
 - users: 普通用户表
 - customer: 客户信息表
 - customer_address: 客户地址表
 - customer_contact: 客户联系人表
 - employee: 员工信息表
 - sample: 样品信息表
 - orders: 订单信息表
 - operation_log: 操作日志表
*/

-- 创建性别枚举类型
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'gender_enum') THEN
        CREATE TYPE gender_enum AS ENUM ('male', 'female');
    END IF;
END $$;

-- ----------------------------
-- 1. 管理员表
-- ----------------------------
DROP TABLE IF EXISTS admin CASCADE;
CREATE TABLE admin (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  name VARCHAR(50) DEFAULT NULL,
  token_version INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_admin_username ON admin(username);
COMMENT ON TABLE admin IS '管理员表';

-- 默认管理员账号
INSERT INTO admin (username, password, name, token_version) VALUES ('admin', 'admin123', '系统管理员', 0);

-- ----------------------------
-- 2. 普通用户表
-- ----------------------------
DROP TABLE IF EXISTS "user" CASCADE;
CREATE TABLE "user" (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  name VARCHAR(50) DEFAULT NULL,
  phone VARCHAR(20) DEFAULT NULL,
  token_version INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_username ON "user"(username);
COMMENT ON TABLE "user" IS '普通用户表';

-- 默认测试用户账号
INSERT INTO "user" (username, password, name, phone, token_version) VALUES ('10001', '123456', '测试用户', '13800138000', 0);

-- ----------------------------
-- 3. 客户信息表
-- ----------------------------
DROP TABLE IF EXISTS customer CASCADE;
CREATE TABLE customer (
  id BIGSERIAL PRIMARY KEY,
  company_name VARCHAR(100) NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_company_name ON customer(company_name);
COMMENT ON TABLE customer IS '客户信息表';

-- 默认客户数据
INSERT INTO customer (company_name) VALUES ('待选择');
INSERT INTO customer (company_name) VALUES ('无');

-- ----------------------------
-- 4. 客户地址表
-- ----------------------------
DROP TABLE IF EXISTS customer_address CASCADE;
CREATE TABLE customer_address (
  id BIGSERIAL PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  address VARCHAR(255) NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_customer_address FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_address_customer_id ON customer_address(customer_id);
COMMENT ON TABLE customer_address IS '客户地址表';

-- ----------------------------
-- 5. 客户联系人表
-- ----------------------------
DROP TABLE IF EXISTS customer_contact CASCADE;
CREATE TABLE customer_contact (
  id BIGSERIAL PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  contact_name VARCHAR(50) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_customer_contact FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_contact_customer_id ON customer_contact(customer_id);
COMMENT ON TABLE customer_contact IS '客户联系人表';

-- ----------------------------
-- 6. 员工信息表
-- ----------------------------
DROP TABLE IF EXISTS employee CASCADE;
CREATE TABLE employee (
  id SERIAL PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  gender gender_enum NOT NULL,
  phone VARCHAR(20) DEFAULT NULL,
  email VARCHAR(100) DEFAULT NULL,
  id_card VARCHAR(18) DEFAULT NULL,
  hire_date DATE DEFAULT NULL
);

CREATE INDEX idx_employee_name ON employee(name);
CREATE INDEX idx_employee_id_card ON employee(id_card);
CREATE INDEX idx_employee_hire_date ON employee(hire_date DESC);
COMMENT ON TABLE employee IS '员工信息表';

-- ----------------------------
-- 7. 样品信息表
-- ----------------------------
DROP TABLE IF EXISTS sample CASCADE;
CREATE TABLE sample (
  id BIGSERIAL PRIMARY KEY,
  customer_id BIGINT DEFAULT NULL,
  alias VARCHAR(50) NOT NULL,
  model VARCHAR(50) NOT NULL,
  color_code VARCHAR(20) DEFAULT NULL,
  image VARCHAR(255) DEFAULT NULL,
  stock INT NOT NULL DEFAULT 0,
  unit_price DECIMAL(10,3) NOT NULL DEFAULT 0.000,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sample_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL
);

CREATE INDEX idx_sample_customer_id ON sample(customer_id);
CREATE INDEX idx_sample_alias ON sample(alias);
CREATE INDEX idx_sample_model ON sample(model);
CREATE INDEX idx_sample_color_code ON sample(color_code);
COMMENT ON TABLE sample IS '样品信息表';

-- ----------------------------
-- 8. 订单信息表
-- ----------------------------
DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  order_number VARCHAR(50) NOT NULL,
  sample_id BIGINT NOT NULL,
  total_quantity INT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  create_date TIMESTAMP NOT NULL,
  delivery_date TIMESTAMP NOT NULL,
  status VARCHAR(20) NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_sample FOREIGN KEY (sample_id) REFERENCES sample(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_orders_sample_id ON orders(sample_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_create_date ON orders(create_date DESC);
CREATE INDEX idx_orders_status_create_date ON orders(status, create_date DESC);
COMMENT ON TABLE orders IS '订单信息表';

-- ----------------------------
-- 9. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS operation_log CASCADE;
CREATE TABLE operation_log (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT DEFAULT NULL,
  operator_name VARCHAR(50) DEFAULT NULL,
  role VARCHAR(20) DEFAULT NULL,
  module VARCHAR(50) NOT NULL,
  action VARCHAR(50) NOT NULL,
  description VARCHAR(500) DEFAULT NULL,
  method VARCHAR(200) DEFAULT NULL,
  request_url VARCHAR(500) DEFAULT NULL,
  request_method VARCHAR(10) DEFAULT NULL,
  request_params TEXT,
  old_data TEXT,
  new_data TEXT,
  response_data TEXT,
  ip_address VARCHAR(50) DEFAULT NULL,
  user_agent VARCHAR(500) DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 1,
  error_msg TEXT,
  duration BIGINT DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_operation_log_user_id ON operation_log(user_id);
CREATE INDEX idx_operation_log_operator_name ON operation_log(operator_name);
CREATE INDEX idx_operation_log_module ON operation_log(module);
CREATE INDEX idx_operation_log_action ON operation_log(action);
CREATE INDEX idx_operation_log_create_time ON operation_log(create_time);
COMMENT ON TABLE operation_log IS '操作日志表';

-- ----------------------------
-- 创建更新时间触发器函数
-- ----------------------------
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为需要自动更新时间的表创建触发器
CREATE TRIGGER trigger_admin_update_time BEFORE UPDATE ON admin FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trigger_user_update_time BEFORE UPDATE ON "user" FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trigger_customer_update_time BEFORE UPDATE ON customer FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trigger_customer_address_update_time BEFORE UPDATE ON customer_address FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trigger_customer_contact_update_time BEFORE UPDATE ON customer_contact FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trigger_sample_update_time BEFORE UPDATE ON sample FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trigger_orders_update_time BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_timestamp();
