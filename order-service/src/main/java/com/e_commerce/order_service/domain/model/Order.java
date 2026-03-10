package com.e_commerce.order_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final UUID userId;
    private final List<OrderItem> items;
    private final BigDecimal total;
    private OrderStatus status;
    private final String address;
    private final LocalDateTime createdAt;

    public Order(UUID userId,
                 String address,
                 List<OrderItem> items,
                 BigDecimal total) {

        this(UUID.randomUUID(), userId, address, items, total, OrderStatus.CREATED, LocalDateTime.now());
    }

    public Order(UUID id,
                 UUID userId,
                 String address,
                 List<OrderItem> items,
                 BigDecimal total,
                 OrderStatus status,
                 LocalDateTime createdAt) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.address = Objects.requireNonNull(address);
        this.items = new ArrayList<>(items);
        this.total = Objects.requireNonNull(total);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public void markAsPaid() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be paid");
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public List<OrderItem> getItems() { return List.copyOf(items); }
    public BigDecimal getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public String getAddress() { return address; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}