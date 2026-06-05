CREATE SCHEMA IF NOT EXISTS restaurant;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE restaurant.approval_status AS ENUM ('APPROVED', 'REJECTED');

CREATE TABLE restaurant.restaurants
(
    id     uuid                NOT NULL,
    name   character varying     NOT NULL,
    active boolean               NOT NULL,
    CONSTRAINT restaurants_pkey PRIMARY KEY (id)
);

CREATE TABLE restaurant.order_approval
(
    id            uuid                       NOT NULL,
    restaurant_id uuid                       NOT NULL,
    order_id      uuid                       NOT NULL,
    status        restaurant.approval_status NOT NULL,
    CONSTRAINT order_approval_pkey PRIMARY KEY (id)
);

CREATE TABLE restaurant.products
(
    id        uuid            NOT NULL,
    name      character varying NOT NULL,
    price     numeric(10, 2)  NOT NULL,
    available boolean         NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

CREATE TABLE restaurant.restaurant_products
(
    id            uuid NOT NULL,
    restaurant_id uuid NOT NULL,
    product_id    uuid NOT NULL,
    CONSTRAINT restaurant_products_pkey PRIMARY KEY (id)
);

ALTER TABLE restaurant.restaurant_products
    ADD CONSTRAINT fk_restaurant_products_restaurant_id
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant.restaurants (id)
            ON DELETE RESTRICT;

ALTER TABLE restaurant.restaurant_products
    ADD CONSTRAINT fk_restaurant_products_product_id
        FOREIGN KEY (product_id)
            REFERENCES restaurant.products (id)
            ON DELETE RESTRICT;
