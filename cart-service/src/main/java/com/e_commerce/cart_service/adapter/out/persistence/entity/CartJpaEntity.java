package com.e_commerce.cart_service.adapter.out.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "carts")
@Getter
@AllArgsConstructor
public class CartJpaEntity{

    @Id
    private UUID id;

    private UUID userId;

    private BigDecimal totalPrice;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItemJpaEntity> items;

    protected CartJpaEntity() {}

}
