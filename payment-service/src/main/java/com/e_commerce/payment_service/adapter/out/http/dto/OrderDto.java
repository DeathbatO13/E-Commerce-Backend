package com.e_commerce.payment_service.adapter.out.http.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Respuesta de GET /orders/{id} en order-service (solo los campos que usa payment-service).
 */
public record OrderDto(
        UUID id,
        UUID userId,
        String status,
        BigDecimal total
) {
}
