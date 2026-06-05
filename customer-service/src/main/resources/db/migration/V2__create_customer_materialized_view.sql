CREATE MATERIALIZED VIEW customer.order_customer_m_view
TABLESPACE pg_default
AS
SELECT id,
       username,
       first_name,
       last_name
FROM customer.customers
WITH DATA;

REFRESH MATERIALIZED VIEW customer.order_customer_m_view;

CREATE OR REPLACE FUNCTION customer.refresh_order_customer_m_view()
RETURNS trigger
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW customer.order_customer_m_view;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER refresh_order_customer_m_view
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
    ON customer.customers
    FOR EACH STATEMENT
    EXECUTE PROCEDURE customer.refresh_order_customer_m_view();
