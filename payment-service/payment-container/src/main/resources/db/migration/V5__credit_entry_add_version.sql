ALTER TABLE payment.credit_entry
    ADD COLUMN version integer NOT NULL DEFAULT 0;