CREATE MATERIALIZED VIEW restaurant.order_restaurant_m_view
TABLESPACE pg_default
AS
SELECT r.id        AS restaurant_id,
       r.name      AS restaurant_name,
       r.active    AS restaurant_active,
       p.id        AS product_id,
       p.name      AS product_name,
       p.price     AS product_price,
       p.available AS product_available
FROM restaurant.restaurants r,
     restaurant.products p,
     restaurant.restaurant_products rp
WHERE r.id = rp.restaurant_id
  AND p.id = rp.product_id
WITH DATA;

REFRESH MATERIALIZED VIEW restaurant.order_restaurant_m_view;

CREATE OR REPLACE FUNCTION restaurant.refresh_order_restaurant_m_view()
RETURNS trigger
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW restaurant.order_restaurant_m_view;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER refresh_order_restaurant_m_view
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
    ON restaurant.restaurant_products
    FOR EACH STATEMENT
    EXECUTE PROCEDURE restaurant.refresh_order_restaurant_m_view();
