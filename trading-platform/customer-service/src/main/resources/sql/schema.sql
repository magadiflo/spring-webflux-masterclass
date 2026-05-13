DROP TABLE IF EXISTS portfolio_items;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(50),
    balance INTEGER
);

CREATE TABLE portfolio_items
(
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT,
    ticker      VARCHAR(10),
    quantity    INTEGER,
    foreign key (customer_id) references customers (id)
);
