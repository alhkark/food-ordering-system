CREATE SCHEMA IF NOT EXISTS payment;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED');

CREATE TABLE payment.payments
(
    id          uuid NOT NULL,
    customer_id uuid NOT NULL,
    order_id    uuid NOT NULL,
    price       numeric(10, 2) NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    status      payment_status NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);

CREATE TABLE payment.credit_entry
(
    id                   uuid NOT NULL,
    customer_id          uuid NOT NULL,
    total_credit_amount  numeric(10, 2) NOT NULL,
    CONSTRAINT credit_entry_pkey PRIMARY KEY (id)
);

CREATE TYPE transaction_type AS ENUM ('DEBIT', 'CREDIT');

CREATE TABLE payment.credit_history
(
    id          uuid NOT NULL,
    customer_id uuid NOT NULL,
    amount      numeric(10, 2) NOT NULL,
    type        transaction_type NOT NULL,
    CONSTRAINT credit_history_pkey PRIMARY KEY (id)
);
