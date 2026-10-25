package com.e_commerce.payment_service.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        String status,
        String transactionId,
        LocalDateTime createdAt
) {}
