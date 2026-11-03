package com.e_commerce.payment_service.domain;

import com.e_commerce.payment_service.domain.model.Payment;
import com.e_commerce.payment_service.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private final UUID orderId = UUID.randomUUID();

    @Test
    void newPayment_getsIdAndTimestamp() {
        Payment payment = new Payment(orderId, BigDecimal.TEN, PaymentStatus.APPROVED, "tx-1");

        assertNotNull(payment.getId());
        assertNotNull(payment.getCreatedAt());
        assertEquals(orderId, payment.getOrderId());
        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
    }

    @Test
    void zeroNegativeOrMissingAmount_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Payment(orderId, BigDecimal.ZERO, PaymentStatus.APPROVED, "tx"));
        assertThrows(IllegalArgumentException.class, () -> new Payment(orderId, new BigDecimal("-1"), PaymentStatus.APPROVED, "tx"));
        assertThrows(IllegalArgumentException.class, () -> new Payment(orderId, null, PaymentStatus.APPROVED, "tx"));
    }

    @Test
    void missingOrderStatusOrTransaction_isRejected() {
        assertThrows(NullPointerException.class, () -> new Payment(null, BigDecimal.TEN, PaymentStatus.APPROVED, "tx"));
        assertThrows(NullPointerException.class, () -> new Payment(orderId, BigDecimal.TEN, null, "tx"));
        assertThrows(NullPointerException.class, () -> new Payment(orderId, BigDecimal.TEN, PaymentStatus.APPROVED, null));
    }
}
