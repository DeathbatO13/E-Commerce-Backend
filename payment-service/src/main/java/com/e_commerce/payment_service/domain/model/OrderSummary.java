package com.e_commerce.payment_service.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos del pedido que payment-service necesita para validar un pago.
 *
 * @param status estado del pedido en order-service (CREATED, PAID, CANCELLED)
 */
public record OrderSummary(UUID id, UUID userId, String status, BigDecimal total) {

    public static final String PENDING_PAYMENT_STATUS = "CREATED";

    public boolean isPendingPayment() {
        return PENDING_PAYMENT_STATUS.equals(status);
    }
}
