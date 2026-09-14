package com.e_commerce.payment_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Payment {

    private final UUID id;
    private final UUID orderId;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String transactionId;
    private final LocalDateTime createdAt;

    public Payment(UUID orderId, BigDecimal amount, PaymentStatus status, String transactionId) {
        this(UUID.randomUUID(), orderId, amount, status, transactionId, LocalDateTime.now());
    }

    public Payment(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status, String transactionId, LocalDateTime createdAt) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        this.id = Objects.requireNonNull(id);
        this.orderId = Objects.requireNonNull(orderId);
        this.amount = amount;
        this.status = Objects.requireNonNull(status);
        this.transactionId = Objects.requireNonNull(transactionId);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
