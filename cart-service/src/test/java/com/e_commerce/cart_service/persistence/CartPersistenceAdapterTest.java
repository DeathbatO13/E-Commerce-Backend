package com.e_commerce.cart_service.persistence;

import com.e_commerce.cart_service.adapter.out.CartPersistenceAdapter;
import com.e_commerce.cart_service.domain.model.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Corre sin la transacción del test (NOT_SUPPORTED), igual que en producción con open-in-view
 * desactivado: si los items no se cargaran en la consulta, el mapper fallaría con
 * LazyInitializationException.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(CartPersistenceAdapter.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CartPersistenceAdapterTest {

    @Autowired
    private CartPersistenceAdapter adapter;

    @Test
    void savedCart_canBeLoadedAndModifiedOutsideTransaction() {
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(userId);
        cart.addProduct(UUID.randomUUID(), BigDecimal.TEN, 2);
        adapter.save(cart);

        Cart loaded = adapter.findByUserId(userId).orElseThrow();
        assertEquals(1, loaded.getItems().size());

        // Antes CartMapper devolvía una lista inmutable y esto lanzaba UnsupportedOperationException
        loaded.addProduct(UUID.randomUUID(), BigDecimal.ONE, 1);
        adapter.save(loaded);

        Cart reloaded = adapter.findByUserId(userId).orElseThrow();
        assertEquals(2, reloaded.getItems().size());
        assertEquals(0, BigDecimal.valueOf(21).compareTo(reloaded.getTotalPrice()));
    }

    @Test
    void clearedCart_removesItems() {
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(userId);
        cart.addProduct(UUID.randomUUID(), BigDecimal.TEN, 1);
        adapter.save(cart);

        Cart loaded = adapter.findByUserId(userId).orElseThrow();
        loaded.clear();
        adapter.save(loaded);

        assertTrue(adapter.findByUserId(userId).orElseThrow().getItems().isEmpty());
    }

    @Test
    void deleteByUserId_deletesTheUsersCart() {
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart(userId);
        cart.addProduct(UUID.randomUUID(), BigDecimal.TEN, 1);
        adapter.save(cart);

        adapter.deleteByUserId(userId);

        assertTrue(adapter.findByUserId(userId).isEmpty());
    }
}
