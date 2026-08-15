DROP TABLE IF EXISTS "order".customers CASCADE;

CREATE TABLE "order".customers
(
    id uuid NOT NULL,
    username text NOT NULL,
    first_name text NOT NULL,
    last_name text NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);