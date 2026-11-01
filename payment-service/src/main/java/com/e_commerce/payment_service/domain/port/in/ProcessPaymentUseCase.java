package com.e_commerce.payment_service.domain.port.in;

import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProcessPaymentUseCase {

    /**
     * Procesa un pago simulado.
     *
     * @param simulatedResult resultado forzado (mock de pasarela); {@code null} equivale a APPROVED
     * @param authorizationToken header Authorization del usuario, para consultar el pedido en su nombre
     */
    Payment processPayment(UUID orderId, BigDecimal amount, PaymentStatus simulatedResult,
                           String authorizationToken);

}
