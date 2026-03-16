DROP TABLE IF EXISTS customer_orders;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS products;

CREATE TABLE customers(
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE products(
    id          BIGSERIAL PRIMARY KEY,
    description VARCHAR(100),
    price       INTEGER
);

CREATE TABLE customer_orders(
    order_id    UUID                     DEFAULT GEN_RANDOM_UUID() PRIMARY KEY,
    customer_id BIGINT,
    product_id  BIGINT,
    amount      INTEGER,
    order_date  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id)
);
