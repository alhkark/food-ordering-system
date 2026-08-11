DROP TYPE IF EXISTS "order".saga_status;
CREATE TYPE "order".saga_status AS ENUM ('STARTED', 'FAILED', 'SUCCEEDED', 'PROCESSING', 'COMPENSATING', 'COMPENSATED');

DROP TYPE IF EXISTS "order".outbox_status;
CREATE TYPE "order".outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS "order".payment_outbox CASCADE;

CREATE TABLE "order".payment_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    saga_status saga_status NOT NULL,
    order_status order_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT payment_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "payment_outbox_saga_status"
    ON "order".payment_outbox
        (type, outbox_status, saga_status);

-- CREATE UNIQUE INDEX "payment_outbox_saga_id"
--     ON "order".payment_outbox
--     (type, saga_id, saga_status);

DROP TABLE IF EXISTS "order".restaurant_approval_outbox CASCADE;

CREATE TABLE "order".restaurant_approval_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    saga_status saga_status NOT NULL,
    order_status order_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT restaurant_approval_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "restaurant_approval_outbox_saga_status"
    ON "order".restaurant_approval_outbox
        (type, outbox_status, saga_status);

-- CREATE UNIQUE INDEX "restaurant_approval_outbox_saga_id"
--     ON "order".restaurant_approval_outbox
--     (type, saga_id, saga_status);