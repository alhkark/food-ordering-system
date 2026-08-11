DROP TYPE IF EXISTS "payment".outbox_status;
CREATE TYPE "payment".outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS "payment".order_outbox CASCADE;

CREATE TABLE "payment".order_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    payment_status payment_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT order_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "payment_order_outbox_saga_status"
    ON "payment".order_outbox
    (type, payment_status);

CREATE UNIQUE INDEX "payment_order_outbox_saga_id_payment_status_outbox_status"
    ON "payment".order_outbox
    (type, saga_id, payment_status, outbox_status);