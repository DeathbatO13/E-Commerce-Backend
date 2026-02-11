package com.e_commerce.catalog_service.service;

import com.e_commerce.catalog_service.application.service.ProductApplicationService;
import com.e_commerce.catalog_service.domain.model.Category;
import com.e_commerce.catalog_service.domain.model.Product;
import com.e_commerce.catalog_service.domain.port.out.CategoryRepository;
import com.e_commerce.catalog_service.domain.port.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductApplicationService.
 * <p>
 * Valida la lógica de negocio principal de creación y desactivación de productos,
 * incluyendo la verificación de existencia de categoría y la delegación al repositorio.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductApplicationService service;

    /**
     * Verifica que se crea correctamente un producto cuando la categoría asociada existe,
     * y que se retorna el producto persistido con estado activo y stock positivo.
     */
    @Test
    void shouldCreateProductSuccessfully() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(categoryId, "Electronics", true);

        when(categoryRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Product product = service.create(new Product(
                UUID.randomUUID(),
                "TV LCD 70 inches",
                "Incredible LCD TV",
                new BigDecimal(1200),
                20,
                new Category(UUID.randomUUID(), "Electronics", true),
                true));

        assertNotNull(product);
        assertEquals("TV LCD 70 inches", product.getName());
        assertTrue(0 < product.getStock());
        assertTrue(product.isActive());
    }

    /**
     * Verifica que al solicitar la desactivación de un producto se delega
     * correctamente la llamada al método deactivate del repositorio.
     */
    @Test
    void shouldDeactivateProduct() {
        UUID id = UUID.randomUUID();

        service.deleteById(id);

        verify(productRepository).deactivate(id);
    }
}