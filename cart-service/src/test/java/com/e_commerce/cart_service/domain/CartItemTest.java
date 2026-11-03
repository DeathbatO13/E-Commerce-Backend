package com.e_commerce.cart_service.domain;

import com.e_commerce.cart_service.domain.model.CartItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void constructor_rejectsInvalidQuantityOrPrice() {
        UUID productId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> new CartItem(UUID.randomUUID(), productId, BigDecimal.TEN, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(UUID.randomUUID(), productId, null, 1));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(UUID.randomUUID(), productId, new BigDecimal("-1"), 1));
    }

    @Test
    void increaseAndUpdateQuantity_rejectNonPositiveValues() {
        CartItem item = new CartItem(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, 2);

        assertThrows(IllegalArgumentException.class, () -> item.increaseQuantity(0));
        assertThrows(IllegalArgumentException.class, () -> item.updateQuantity(-1));
        assertEquals(2, item.getQuantity());
    }

    @Test
    void subtotal_isPriceTimesQuantity() {
        CartItem item = new CartItem(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("2.50"), 4);

        assertEquals(0, new BigDecimal("10.00").compareTo(item.calculateSubtotal()));
    }
}
