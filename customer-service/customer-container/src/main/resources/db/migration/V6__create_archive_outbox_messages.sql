DROP TABLE IF EXISTS "customer".archive_customer_outbox CASCADE;

CREATE TABLE "customer".archive_customer_outbox
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT archive_customer_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "archive_customer_outbox_status"
    ON "customer".archive_customer_outbox (outbox_status);