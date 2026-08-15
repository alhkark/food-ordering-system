CREATE SCHEMA IF NOT EXISTS customer;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE customer.customers
(
    id         uuid NOT NULL,
    username   character varying NOT NULL,
    first_name character varying NOT NULL,
    last_name  character varying NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);
