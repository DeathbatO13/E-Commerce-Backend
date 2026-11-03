package com.e_commerce.catalog_service.persistence;

import com.e_commerce.catalog_service.adapter.out.persistence.CategoryAdapter;
import com.e_commerce.catalog_service.adapter.out.persistence.ProductAdapter;
import com.e_commerce.catalog_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Adaptadores JPA de catalog contra H2: mapeo entidad ↔ dominio y borrado lógico (active = false).
 * Después de cada escritura se hace flush + clear para que la lectura vaya a la base y no al
 * contexto de persistencia.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({ProductAdapter.class, CategoryAdapter.class})
class CatalogPersistenceAdapterTest {

    @Autowired
    private ProductAdapter productAdapter;

    @Autowired
    private CategoryAdapter categoryAdapter;

    @Autowired
    private TestEntityManager em;

    private Category savedCategory(String name) {
        return categoryAdapter.save(new Category(UUID.randomUUID(), name, true));
    }

    private Product savedProduct(String name, Category category) {
        return productAdapter.save(new Product(UUID.randomUUID(), name, "desc",
                new BigDecimal("99.90"), 5, new Category(category.getId(), null, true), true));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void savedProduct_isLoadedWithAllFieldsAndItsCategory() {
        Category category = savedCategory("Electronics");
        Product saved = savedProduct("TV", category);
        flushAndClear();

        Product loaded = productAdapter.findById(saved.getId()).orElseThrow();

        assertEquals("TV", loaded.getName());
        assertEquals("desc", loaded.getDescription());
        assertEquals(0, new BigDecimal("99.90").compareTo(loaded.getPrice()));
        assertEquals(5, loaded.getStock());
        assertTrue(loaded.isActive());
        assertEquals(category.getId(), loaded.getCategory().getId());
        assertEquals("Electronics", loaded.getCategory().getName());
    }

    @Test
    void savingProductWithUnknownCategory_fails() {
        Product product = new Product(UUID.randomUUID(), "TV", "desc", BigDecimal.TEN, 1,
                new Category(UUID.randomUUID(), null, true), true);

        assertThrows(ResourceNotFoundException.class, () -> productAdapter.save(product));
    }

    @Test
    void deactivatedProduct_disappearsFromQueries() {
        Category category = savedCategory("Electronics");
        Product kept = savedProduct("TV Samsung", category);
        Product removed = savedProduct("TV LG", category);
        flushAndClear();

        // deactivate se apoya en el dirty checking de la transacción (no llama a save)
        productAdapter.deactivate(removed.getId());
        flushAndClear();

        assertTrue(productAdapter.findById(removed.getId()).isEmpty());
        assertEquals(1, productAdapter.findAll().size());
        assertEquals(kept.getId(), productAdapter.findByCategory(category.getId()).get(0).getId());
    }

    @Test
    void findByName_isPartialCaseInsensitiveAndOnlyActive() {
        Category category = savedCategory("Electronics");
        savedProduct("Smart TV", category);
        Product inactive = savedProduct("TV antiguo", category);
        savedProduct("Laptop", category);
        productAdapter.deactivate(inactive.getId());
        flushAndClear();

        var result = productAdapter.findByName("tv");

        assertEquals(1, result.size());
        assertEquals("Smart TV", result.get(0).getName());
    }

    @Test
    void existsByCategory_onlyCountsActiveProducts() {
        Category category = savedCategory("Electronics");
        Product product = savedProduct("TV", category);
        flushAndClear();
        assertTrue(productAdapter.existsByCategory(category.getId()));

        productAdapter.deactivate(product.getId());
        flushAndClear();

        assertFalse(productAdapter.existsByCategory(category.getId()));
    }

    @Test
    void deactivatedCategory_countsAsMissing() {
        Category kept = savedCategory("Books");
        Category removed = savedCategory("Toys");
        flushAndClear();

        categoryAdapter.deactivate(removed.getId());
        flushAndClear();

        assertFalse(categoryAdapter.existsById(removed.getId()));
        assertTrue(categoryAdapter.findById(removed.getId()).isEmpty());
        assertTrue(categoryAdapter.findByName("Toys").isEmpty());
        assertEquals(1, categoryAdapter.findAll().size());
        assertEquals(kept.getId(), categoryAdapter.findAll().get(0).getId());
    }

    @Test
    void deactivatingUnknownProductOrCategory_fails() {
        assertThrows(ResourceNotFoundException.class, () -> productAdapter.deactivate(UUID.randomUUID()));
        assertThrows(ResourceNotFoundException.class, () -> categoryAdapter.deactivate(UUID.randomUUID()));
    }
}
