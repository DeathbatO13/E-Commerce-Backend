package com.e_commerce.order_service.adapter.out.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
@Setter
@Getter
public class OrderItemEntity {

    @Id
    @GeneratedValue
    private Long id;

    private UUID productId;

    private int quantity;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

}
