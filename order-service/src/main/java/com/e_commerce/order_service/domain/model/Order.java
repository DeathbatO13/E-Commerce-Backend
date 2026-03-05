package com.e_commerce.order_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order{

    private final UUID id;
    private final UUID userId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private String address;
    private final LocalDateTime createdAt;


    public Order(UUID id, UUID userId, List<OrderItem> items, OrderStatus status, String address, LocalDateTime createdAt) {

        if(items == null || items.isEmpty())
            throw new IllegalArgumentException("Order must contain at least one item");

        this.id = id;
        this.userId = userId;
        this.items = items;
        this.status = status;
        this.address = address;
        this.createdAt = createdAt;
    }

    //Constructor para nuevo pedido
    public Order(UUID userId, String address, List<OrderItem> items){
        this(UUID.randomUUID(), userId, items, OrderStatus.CREATED, address, LocalDateTime.now());
    }


    public BigDecimal calculateTotal(){
        return items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markAsPaid(){
        if(this.status != OrderStatus.CREATED){
            throw new IllegalArgumentException("Only CREATED orders can be paid");
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }


    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
