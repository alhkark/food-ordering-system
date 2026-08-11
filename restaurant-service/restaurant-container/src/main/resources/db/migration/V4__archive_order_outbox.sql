DROP TABLE IF EXISTS restaurant.archive_order_outbox CASCADE;

CREATE TABLE restaurant.archive_order_outbox
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
    CONSTRAINT archive_order_outbox_pkey PRIMARY KEY (id)
);