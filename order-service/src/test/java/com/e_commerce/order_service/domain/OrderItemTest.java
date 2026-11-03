package com.e_commerce.order_service.domain;

import com.e_commerce.order_service.domain.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void constructor_rejectsInvalidQuantityOrPrice() {
        UUID productId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> new OrderItem(productId, 0, BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(productId, -1, BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(productId, 1, null));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(productId, 1, new BigDecimal("-0.01")));
    }

    @Test
    void subtotal_isPriceTimesQuantity() {
        OrderItem item = new OrderItem(UUID.randomUUID(), 3, new BigDecimal("4.20"));

        assertEquals(0, new BigDecimal("12.60").compareTo(item.getSubtotal()));
    }
}
