package com.e_commerce.payment_service.domain.port.in;

import com.e_commerce.payment_service.domain.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProcessPaymentUseCase {

    Payment processPayment(UUID orderId, BigDecimal amount, String paymentToken);

}
