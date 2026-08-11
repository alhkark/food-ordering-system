DROP TYPE IF EXISTS restaurant.outbox_status;
CREATE TYPE restaurant.outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS restaurant.order_outbox CASCADE;

CREATE TABLE restaurant.order_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    approval_status approval_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT order_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "restaurant_order_outbox_saga_status"
    ON "restaurant".order_outbox
        (type, approval_status);

CREATE UNIQUE INDEX "restaurant_order_outbox_saga_id"
    ON "restaurant".order_outbox
        (type, saga_id, approval_status, outbox_status);