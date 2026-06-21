package com.e_commerce.cart_service.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    private final UUID id;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    private final UUID productId;
    private final BigDecimal price;
    private int quantity;

    public CartItem(UUID id, UUID productId, BigDecimal price, int quantity) {

        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        this.id = id;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity(int amount){

        if(amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");

        quantity += amount;

    }

    public void updateQuantity(int quantity){

        if(quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        this.quantity = quantity;
    }

    public BigDecimal calculateSubtotal(){

        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
