-- V1__init_reservation_schema.sql

CREATE TABLE stocks (
    product_id VARCHAR(255) PRIMARY KEY,
    quantity INTEGER NOT NULL
);