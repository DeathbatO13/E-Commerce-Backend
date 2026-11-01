package com.e_commerce.payment_service.adapter.in.rest.dto;

import com.e_commerce.payment_service.domain.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentRequest(
        @NotNull(message = "Order ID is required")
        UUID orderId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        // Opcional (mock): APPROVED | REJECTED. Si no se envía, el pago se aprueba.
        PaymentStatus simulateResult
) {}
