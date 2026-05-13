INSERT INTO customers(name, email)
VALUES ('sam', 'sam@gmail.com'),
       ('mike', 'mike@gmail.com'),
       ('jake', 'jake@gmail.com'),
       ('emily', 'emily@example.com'),
       ('sophia', 'sophia@example.com'),
       ('liam', 'liam@example.com'),
       ('olivia', 'olivia@example.com'),
       ('noah', 'noah@example.com'),
       ('ava', 'ava@example.com'),
       ('ethan', 'ethan@example.com');

INSERT INTO products(description, price)
VALUES ('iphone 20', 1000),
       ('iphone 18', 750),
       ('ipad', 800),
       ('mac pro', 3000),
       ('apple watch', 400),
       ('macbook air', 1200),
       ('airpods pro', 250),
       ('imac', 2000),
       ('apple tv', 200),
       ('homepod', 300);

-- Order 1: sam buys an iphone 20 & iphone 18
INSERT INTO customer_orders(customer_id, product_id, amount, order_date)
VALUES (1, 1, 950, CURRENT_TIMESTAMP),
       (1, 2, 850, CURRENT_TIMESTAMP);

-- Order 2: mike buys an iphone 20 and mac pro
INSERT INTO customer_orders(customer_id, product_id, amount, order_date)
VALUES (2, 1, 975, CURRENT_TIMESTAMP),
       (2, 4, 2999, CURRENT_TIMESTAMP);

-- Order 3: jake buys an iphone 18 & ipad
INSERT INTO customer_orders(customer_id, product_id, amount, order_date)
VALUES (3, 2, 750, CURRENT_TIMESTAMP),
       (3, 2, 775, CURRENT_TIMESTAMP);
