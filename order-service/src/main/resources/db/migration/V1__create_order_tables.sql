CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    address VARCHAR(255),
    total NUMERIC(12, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_orders_user_id ON orders(user_id);

-- OrderItemEntity.id usa @GeneratedValue (AUTO): Hibernate pide bloques de 50 a esta secuencia
CREATE SEQUENCE order_items_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
