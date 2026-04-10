-- ============================================================
-- CREACION DE BASE DE DATOS PARA SISTEMA HORIZONTES SIN LIMITES
-- IPC2 - Proyecto 1
-- ============================================================

CREATE DATABASE IF NOT EXISTS horizontes_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE horizontes_db;

-- ============================================================
-- CREACION DE TABLAS
-- ============================================================

-- TABLA DE USUARIOS DEL SISTEMA
CREATE TABLE usuario (
    id      INT          PRIMARY KEY AUTO_INCREMENT,
    nombre  VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    tipo    TINYINT      NOT NULL COMMENT '1=Atencion Cliente, 2=Operaciones, 3=Administrador',
    activo  TINYINT(1)   NOT NULL DEFAULT 1,
    UNIQUE KEY uq_nombre (nombre)
);

-- TABLA DE DESTINOS TURISTICOS
CREATE TABLE destino (
    id          INT          PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    pais        VARCHAR(100) NOT NULL,
    descripcion TEXT,
    clima       VARCHAR(200),
    imagen_url  VARCHAR(500),
    UNIQUE KEY uq_nombre (nombre)
);

-- TABLA DE PROVEEDORES DE SERVICIOS
CREATE TABLE proveedor (
    id       INT          PRIMARY KEY AUTO_INCREMENT,
    nombre   VARCHAR(100) NOT NULL,
    tipo     TINYINT      NOT NULL COMMENT '1=Aerolinea, 2=Hotel, 3=Tour, 4=Traslado, 5=Otro',
    pais     VARCHAR(100) NOT NULL,
    contacto VARCHAR(200),
    UNIQUE KEY uq_nombre (nombre)
);

-- TABLA DE PAQUETES TURISTICOS
CREATE TABLE paquete (
    id           INT            PRIMARY KEY AUTO_INCREMENT,
    nombre       VARCHAR(150)   NOT NULL,
    destino_id   INT            NOT NULL,
    duracion     INT            NOT NULL COMMENT 'En dias',
    descripcion  TEXT,
    precio_venta DECIMAL(10,2)  NOT NULL,
    capacidad    INT            NOT NULL,
    activo       TINYINT(1)     NOT NULL DEFAULT 1,
    UNIQUE KEY uq_nombre (nombre)
);

-- TABLA DE SERVICIOS INCLUIDOS EN CADA PAQUETE
CREATE TABLE servicio_paquete (
    id           INT            PRIMARY KEY AUTO_INCREMENT,
    paquete_id   INT            NOT NULL,
    proveedor_id INT            NOT NULL,
    descripcion  VARCHAR(300)   NOT NULL,
    costo        DECIMAL(10,2)  NOT NULL
);

-- TABLA DE CLIENTES
CREATE TABLE cliente (
    id               INT          PRIMARY KEY AUTO_INCREMENT,
    dpi              VARCHAR(20)  NOT NULL,
    nombre           VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    telefono         VARCHAR(20),
    email            VARCHAR(100),
    nacionalidad     VARCHAR(100),
    UNIQUE KEY uq_dpi (dpi)
);

-- TABLA DE RESERVACIONES
CREATE TABLE reservacion (
    id             INT            PRIMARY KEY AUTO_INCREMENT,
    numero         VARCHAR(15)    NOT NULL COMMENT 'Formato RES-00001',
    fecha_creacion DATE           NOT NULL,
    fecha_viaje    DATE           NOT NULL,
    paquete_id     INT            NOT NULL,
    agente_id      INT            NOT NULL,
    costo_total    DECIMAL(10,2)  NOT NULL,
    estado         VARCHAR(20)    NOT NULL DEFAULT 'PENDIENTE' 
                   COMMENT 'PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA',
    UNIQUE KEY uq_numero (numero)
);

-- TABLA INTERMEDIA ENTRE RESERVACIONES Y PASAJEROS
CREATE TABLE reservacion_pasajero (
    reservacion_id INT NOT NULL,
    cliente_id     INT NOT NULL,
    PRIMARY KEY (reservacion_id, cliente_id)
);

-- TABLA DE PAGOS
CREATE TABLE pago (
    id             INT           PRIMARY KEY AUTO_INCREMENT,
    reservacion_id INT           NOT NULL,
    monto          DECIMAL(10,2) NOT NULL,
    metodo         TINYINT       NOT NULL COMMENT '1=Efectivo, 2=Tarjeta, 3=Transferencia',
    fecha          DATE          NOT NULL
);

-- TABLA DE CANCELACIONES Y REEMBOLSOS
CREATE TABLE cancelacion (
    id                   INT           PRIMARY KEY AUTO_INCREMENT,
    reservacion_id       INT           NOT NULL,
    fecha_cancelacion    DATE          NOT NULL,
    monto_reembolso      DECIMAL(10,2) NOT NULL,
    porcentaje_reembolso INT           NOT NULL COMMENT '100, 70 o 40',
    UNIQUE KEY uq_reservacion (reservacion_id)
);

-- ============================================================
-- AGREGANDO LLAVES FORANEAS
-- ============================================================

-- Paquete pertenece a un Destino
ALTER TABLE paquete
ADD CONSTRAINT FK_PAQUETE_DESTINO
FOREIGN KEY (destino_id) REFERENCES destino(id);

-- Servicio_Paquete pertenece a un Paquete
ALTER TABLE servicio_paquete
ADD CONSTRAINT FK_SERVICIO_PAQUETE
FOREIGN KEY (paquete_id) REFERENCES paquete(id);

-- Servicio_Paquete es provisto por un Proveedor
ALTER TABLE servicio_paquete
ADD CONSTRAINT FK_SERVICIO_PROVEEDOR
FOREIGN KEY (proveedor_id) REFERENCES proveedor(id);

-- Reservacion usa un Paquete
ALTER TABLE reservacion
ADD CONSTRAINT FK_RESERVACION_PAQUETE
FOREIGN KEY (paquete_id) REFERENCES paquete(id);

-- Reservacion fue creada por un Agente (Usuario)
ALTER TABLE reservacion
ADD CONSTRAINT FK_RESERVACION_AGENTE
FOREIGN KEY (agente_id) REFERENCES usuario(id);

-- Reservacion_Pasajero conecta Reservaciones con Clientes
ALTER TABLE reservacion_pasajero
ADD CONSTRAINT FK_RESPAS_RESERVACION
FOREIGN KEY (reservacion_id) REFERENCES reservacion(id);

ALTER TABLE reservacion_pasajero
ADD CONSTRAINT FK_RESPAS_CLIENTE
FOREIGN KEY (cliente_id) REFERENCES cliente(id);

-- Pago pertenece a una Reservacion
ALTER TABLE pago
ADD CONSTRAINT FK_PAGO_RESERVACION
FOREIGN KEY (reservacion_id) REFERENCES reservacion(id);

-- Cancelacion pertenece a una Reservacion
ALTER TABLE cancelacion
ADD CONSTRAINT FK_CANCELACION_RESERVACION
FOREIGN KEY (reservacion_id) REFERENCES reservacion(id);

-- ============================================================
-- DATOS INICIALES
-- ============================================================

-- Usuario administrador por defecto
INSERT INTO usuario (nombre, password, tipo, activo)
VALUES ('admin', 'admin123', 3, 1);