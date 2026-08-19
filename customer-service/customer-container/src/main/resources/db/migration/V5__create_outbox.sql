DROP TYPE IF EXISTS "customer".outbox_status;
CREATE TYPE "customer".outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS "customer".customer_outbox CASCADE;

CREATE TABLE "customer".customer_outbox
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT customer_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "customer_outbox_status"
    ON "customer".customer_outbox (outbox_status);