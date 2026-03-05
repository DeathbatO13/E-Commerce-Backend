package com.e_commerce.order_service.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final BigDecimal price;

    public OrderItem(UUID productId, int quantity, BigDecimal price) {

        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSubtotal(){
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
