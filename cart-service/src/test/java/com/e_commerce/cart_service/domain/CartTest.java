package com.e_commerce.cart_service.domain;

import com.e_commerce.cart_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.cart_service.domain.model.Cart;
import com.e_commerce.cart_service.domain.model.CartItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Reglas de {@link Cart} sin Spring: total, fusión de items y cantidades inválidas. */
class CartTest {

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    @Test
    void newCart_isEmptyWithZeroTotal() {
        Cart cart = Cart.create(userId);

        assertNotNull(cart.getId());
        assertEquals(userId, cart.getUserId());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalPrice()));
    }

    @Test
    void addProduct_recalculatesTotal() {
        Cart cart = Cart.create(userId);

        cart.addProduct(productId, new BigDecimal("12.50"), 2);
        cart.addProduct(UUID.randomUUID(), BigDecimal.TEN, 1);

        assertEquals(2, cart.getItems().size());
        assertEquals(0, new BigDecimal("35.00").compareTo(cart.getTotalPrice()));
    }

    @Test
    void addSameProductTwice_mergesQuantity() {
        Cart cart = Cart.create(userId);

        cart.addProduct(productId, BigDecimal.TEN, 1);
        cart.addProduct(productId, BigDecimal.TEN, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(0, BigDecimal.valueOf(40).compareTo(cart.getTotalPrice()));
    }

    @Test
    void addProduct_withZeroOrNegativeQuantity_fails() {
        Cart cart = Cart.create(userId);

        assertThrows(IllegalArgumentException.class, () -> cart.addProduct(productId, BigDecimal.TEN, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addProduct(productId, BigDecimal.TEN, -2));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void updateQuantity_replacesQuantity() {
        Cart cart = Cart.create(userId);
        cart.addProduct(productId, BigDecimal.TEN, 1);

        cart.updateQuantity(productId, 5);

        assertEquals(5, cart.getItems().get(0).getQuantity());
        assertEquals(0, BigDecimal.valueOf(50).compareTo(cart.getTotalPrice()));
    }

    @Test
    void updateQuantity_toZero_removesItem() {
        Cart cart = Cart.create(userId);
        cart.addProduct(productId, BigDecimal.TEN, 2);

        cart.updateQuantity(productId, 0);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalPrice()));
    }

    @Test
    void updateQuantity_ofProductNotInCart_fails() {
        Cart cart = Cart.create(userId);

        assertThrows(ResourceNotFoundException.class, () -> cart.updateQuantity(productId, 1));
    }

    @Test
    void removeProduct_recalculatesTotal() {
        Cart cart = Cart.create(userId);
        UUID other = UUID.randomUUID();
        cart.addProduct(productId, BigDecimal.TEN, 1);
        cart.addProduct(other, BigDecimal.ONE, 3);

        cart.removeProduct(productId);

        assertEquals(1, cart.getItems().size());
        assertEquals(other, cart.getItems().get(0).getProductId());
        assertEquals(0, BigDecimal.valueOf(3).compareTo(cart.getTotalPrice()));
    }

    @Test
    void clear_removesItemsAndResetsTotal() {
        Cart cart = Cart.create(userId);
        cart.addProduct(productId, BigDecimal.TEN, 2);

        cart.clear();

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotalPrice()));
    }

    @Test
    void items_areReadOnlyAndDecoupledFromConstructorList() {
        List<CartItem> source = new ArrayList<>(List.of(new CartItem(UUID.randomUUID(), productId, BigDecimal.TEN, 1)));
        Cart cart = new Cart(BigDecimal.TEN, UUID.randomUUID(), userId, source);

        source.clear();

        assertEquals(1, cart.getItems().size());
        assertThrows(UnsupportedOperationException.class, () -> cart.getItems().clear());
    }
}
