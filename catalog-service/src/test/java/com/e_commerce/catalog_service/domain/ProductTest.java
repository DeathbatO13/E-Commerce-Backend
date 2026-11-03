package com.e_commerce.catalog_service.domain;

import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Invariantes del constructor de {@link Product}. */
class ProductTest {

    private final Category category = new Category(UUID.randomUUID(), "Electronics", true);

    private Product product(String name, BigDecimal price, Integer stock, Category category) {
        return new Product(UUID.randomUUID(), name, "desc", price, stock, category, true);
    }

    @Test
    void validProduct_keepsItsData() {
        Product product = product("TV", BigDecimal.TEN, 0, category);

        assertEquals("TV", product.getName());
        assertEquals(0, product.getStock());
        assertSame(category, product.getCategory());
        assertTrue(product.isActive());
    }

    @Test
    void blankName_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> product(null, BigDecimal.TEN, 1, category));
        assertThrows(IllegalArgumentException.class, () -> product("  ", BigDecimal.TEN, 1, category));
    }

    @Test
    void zeroNegativeOrMissingPrice_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> product("TV", BigDecimal.ZERO, 1, category));
        assertThrows(IllegalArgumentException.class, () -> product("TV", new BigDecimal("-5"), 1, category));
        assertThrows(IllegalArgumentException.class, () -> product("TV", null, 1, category));
    }

    @Test
    void negativeOrMissingStock_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> product("TV", BigDecimal.TEN, -1, category));
        assertThrows(IllegalArgumentException.class, () -> product("TV", BigDecimal.TEN, null, category));
    }

    @Test
    void missingCategory_isRejected() {
        assertThrows(NullPointerException.class, () -> product("TV", BigDecimal.TEN, 1, null));
    }
}
