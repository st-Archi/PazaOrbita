-- =====================================================================
-- Plaza Órbita - Esquema de base de datos MySQL
-- =====================================================================
CREATE DATABASE IF NOT EXISTS plaza_orbita CHARACTER SET utf8mb4;
USE plaza_orbita;

-- ---------------------------------------------------------------------
-- Usuarios (los 3 roles: ADMIN, BUSINESS_OWNER, CUSTOMER)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(120)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    role          ENUM('ADMIN','BUSINESS_OWNER','CUSTOMER') NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Negocios dentro de la plaza
-- ---------------------------------------------------------------------
CREATE TABLE businesses (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id      BIGINT NOT NULL,
    name          VARCHAR(150) NOT NULL,
    category      ENUM('PRODUCT','SERVICE') NOT NULL,
    location      VARCHAR(120),
    opens_at      TIME,
    closes_at     TIME,
    status        ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Catálogo / inventario (negocios de producto)
-- ---------------------------------------------------------------------
CREATE TABLE products (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id    BIGINT NOT NULL,
    name           VARCHAR(150) NOT NULL,
    description    VARCHAR(500),
    price          DECIMAL(10,2) NOT NULL,
    stock          INT NOT NULL DEFAULT 0,
    min_threshold  INT NOT NULL DEFAULT 5,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Citas (negocios de servicio)
-- ---------------------------------------------------------------------
CREATE TABLE appointments (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id   BIGINT NOT NULL,
    customer_id   BIGINT NOT NULL,
    service_name  VARCHAR(150) NOT NULL,
    appt_date     DATE NOT NULL,
    appt_time     TIME NOT NULL,
    status        ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uq_no_double_booking (business_id, appt_date, appt_time)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Pedidos (negocios de producto) + detalle
-- ---------------------------------------------------------------------
CREATE TABLE orders (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id   BIGINT NOT NULL,
    customer_id   BIGINT NOT NULL,
    status        ENUM('PENDING','READY_FOR_PICKUP','DELIVERED','CANCELLED') DEFAULT 'PENDING',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE order_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Notificaciones
-- ---------------------------------------------------------------------
CREATE TABLE notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    message     VARCHAR(300) NOT NULL,
    type        ENUM('APPOINTMENT','ORDER','SYSTEM') NOT NULL,
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Índices de apoyo para reportes
CREATE INDEX idx_orders_business ON orders(business_id);
CREATE INDEX idx_appt_business ON appointments(business_id);
CREATE INDEX idx_products_business ON products(business_id);
