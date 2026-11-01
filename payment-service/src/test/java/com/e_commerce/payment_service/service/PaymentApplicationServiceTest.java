package com.e_commerce.payment_service.service;

import com.e_commerce.payment_service.application.service.PaymentApplicationService;
import com.e_commerce.payment_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.payment_service.domain.model.OrderSummary;
import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.model.PaymentStatus;
import com.e_commerce.payment_service.domain.port.out.OrderPort;
import com.e_commerce.payment_service.domain.port.out.PaymentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentApplicationServiceTest {

    private static final String TOKEN = "Bearer user-token";

    @Mock
    private PaymentRepositoryPort paymentRepository;

    @Mock
    private OrderPort orderPort;

    private PaymentApplicationService service;

    private final UUID orderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new PaymentApplicationService(paymentRepository, orderPort);
        lenient().when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void orderWith(String status, BigDecimal total) {
        when(orderPort.getOrder(orderId, TOKEN))
                .thenReturn(new OrderSummary(orderId, UUID.randomUUID(), status, total));
    }

    @Test
    void approvedPayment_marksOrderAsPaid() {
        orderWith("CREATED", new BigDecimal("150.00"));

        Payment payment = service.processPayment(orderId, new BigDecimal("150"), null, TOKEN);

        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        verify(paymentRepository).save(any(Payment.class));
        verify(orderPort).markOrderAsPaid(orderId);
    }

    @Test
    void rejectedPayment_isSavedButOrderIsNotPaid() {
        orderWith("CREATED", new BigDecimal("150.00"));

        Payment payment = service.processPayment(orderId, new BigDecimal("150.00"), PaymentStatus.REJECTED, TOKEN);

        assertEquals(PaymentStatus.REJECTED, payment.getStatus());
        verify(paymentRepository).save(any(Payment.class));
        verify(orderPort, never()).markOrderAsPaid(any());
    }

    @Test
    void amountDifferentFromOrderTotal_isRejectedWithoutSaving() {
        orderWith("CREATED", new BigDecimal("150.00"));

        assertThrows(IllegalArgumentException.class,
                () -> service.processPayment(orderId, new BigDecimal("1.00"), null, TOKEN));

        verify(paymentRepository, never()).save(any());
        verify(orderPort, never()).markOrderAsPaid(any());
    }

    @Test
    void orderAlreadyPaid_isConflict() {
        orderWith("PAID", new BigDecimal("150.00"));

        assertThrows(IllegalStateException.class,
                () -> service.processPayment(orderId, new BigDecimal("150.00"), null, TOKEN));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void missingOrder_propagatesNotFound() {
        when(orderPort.getOrder(orderId, TOKEN)).thenThrow(new ResourceNotFoundException("Order not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> service.processPayment(orderId, new BigDecimal("150.00"), null, TOKEN));

        verify(paymentRepository, never()).save(any());
    }
}
