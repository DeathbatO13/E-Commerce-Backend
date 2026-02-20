package com.e_commerce.cart_service.adapter.out.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "cart_items")
public class CartItemJpaEntity{

    @Id
    private UUID id;

    private UUID productId;

    private BigDecimal price;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartJpaEntity cart;

    protected CartItemJpaEntity() {}

    public CartItemJpaEntity(UUID id, UUID productId, BigDecimal price, int quantity, CartJpaEntity cart) {
        this.id = id;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.cart = cart;
    }

}
