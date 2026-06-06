-- V1__init_packing_schema.sql

CREATE TABLE packing_tasks (
    order_id VARCHAR(255) PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    box_size VARCHAR(50),
    weight DOUBLE PRECISION
);

CREATE TABLE box_types (
    size VARCHAR(50) PRIMARY KEY,
    length DOUBLE PRECISION NOT NULL,
    width DOUBLE PRECISION NOT NULL,
    height DOUBLE PRECISION NOT NULL
);