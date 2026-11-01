package com.e_commerce.payment_service.application.service;

import com.e_commerce.payment_service.domain.model.OrderSummary;
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
    public Payment processPayment(UUID orderId, BigDecimal amount, PaymentStatus simulatedResult,
                                  String authorizationToken) {

        // order-service aplica el ownership: un CLIENT recibe 403 si el pedido es de otro usuario
        OrderSummary order = orderPort.getOrder(orderId, authorizationToken);

        if (!order.isPendingPayment()) {
            throw new IllegalStateException("Order " + orderId + " is not pending payment (status: "
                    + order.status() + ")");
        }
        if (order.total() == null || order.total().compareTo(amount) != 0) {
            throw new IllegalArgumentException("Amount " + amount + " does not match order total "
                    + order.total());
        }

        // Pasarela simulada: el cliente puede forzar REJECTED para probar ese flujo
        PaymentStatus status = simulatedResult != null ? simulatedResult : PaymentStatus.APPROVED;
        String transactionId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment savedPayment = paymentRepository.save(new Payment(orderId, amount, status, transactionId));

        if (status == PaymentStatus.APPROVED) {
            orderPort.markOrderAsPaid(orderId);
        }

        return savedPayment;
    }
}
