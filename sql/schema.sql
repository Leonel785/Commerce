-- =====================================================
-- SISTEMA E-COMMERCE - ESQUEMA DE BASE DE DATOS
-- MySQL 8.0+
-- =====================================================

CREATE DATABASE IF NOT EXISTS ecommerce_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

-- -----------------------------------------------------
-- Tabla: usuario
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('CLIENTE', 'ADMIN') NOT NULL DEFAULT 'CLIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Tabla: cliente
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Tabla: producto
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Tabla: carrito
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL UNIQUE,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Tabla: detalle_carrito
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (carrito_id) REFERENCES carrito(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE,
    UNIQUE KEY uk_carrito_producto (carrito_id, producto_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Tabla: pedido
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADO', 'ENVIADO', 'ENTREGADO') NOT NULL DEFAULT 'PENDIENTE',
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- Tabla: detalle_pedido
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- DATOS DE PRUEBA
-- =====================================================

-- Usuarios (password: '123456' encriptado con BCrypt)
INSERT INTO usuario (username, password, rol) VALUES
('admin', '123456', 'ADMIN'),
('cliente1', '123456', 'CLIENTE'),
('cliente2', '123456', 'CLIENTE');

-- Clientes
INSERT INTO cliente (usuario_id, nombres, correo, direccion, telefono) VALUES
(2, 'Juan Perez', 'juan@email.com', 'Av. Principal 123, Lima', '987654321'),
(3, 'Maria Garcia', 'maria@email.com', 'Calle Secundaria 456, Lima', '912345678');

-- Productos
INSERT INTO producto (nombre, categoria, precio, stock) VALUES
('Laptop HP Pavilion', 'Tecnologia', 2499.00, 15),
('Mouse Logitech G203', 'Tecnologia', 89.90, 50),
('Teclado Mecanico Redragon', 'Tecnologia', 199.00, 30),
('Monitor Samsung 24"', 'Tecnologia', 699.00, 20),
('Audifonos Sony WH-1000XM5', 'Tecnologia', 1299.00, 10),
('Camisa Polo Azul', 'Ropa', 79.90, 100),
('Jeans Clasicos', 'Ropa', 129.90, 80),
('Zapatillas Deportivas', 'Calzado', 249.90, 60),
('Mochila Escolar', 'Accesorios', 89.90, 40),
('Botella Termica', 'Accesorios', 49.90, 200);

-- Carritos
INSERT INTO carrito (cliente_id, fecha, total) VALUES
(1, NOW(), 0.00),
(2, NOW(), 0.00);
