package com.e_commerce.payment_service.application.service;

import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.model.PaymentStatus;
import com.e_commerce.payment_service.domain.port.in.ProcessPaymentUseCase;
import com.e_commerce.payment_service.domain.port.out.OrderPort;
import com.e_commerce.payment_service.domain.port.out.PaymentRepositoryPort;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentApplicationService implements ProcessPaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final OrderPort orderPort;

    public PaymentApplicationService(PaymentRepositoryPort paymentRepository, OrderPort orderPort) {
        this.paymentRepository = paymentRepository;
        this.orderPort = orderPort;
    }

    @Override
    public Payment processPayment(UUID orderId, BigDecimal amount, String paymentToken) {
        // Simulación del resultado del pago (Mock pasarela)
        // Se aprueba si la transacción o monto es válido
        boolean isApproved = amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
        PaymentStatus status = isApproved ? PaymentStatus.APPROVED : PaymentStatus.REJECTED;
        String transactionId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = new Payment(orderId, amount, status, transactionId);
        Payment savedPayment = paymentRepository.save(payment);

        if (isApproved) {
            orderPort.markOrderAsPaid(orderId);
        }

        return savedPayment;
    }
}
