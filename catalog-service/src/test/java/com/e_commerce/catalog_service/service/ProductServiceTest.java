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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductApplicationService service;

    @Test
    void shouldCreateProductSuccessfully(){

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
                new Category(UUID.randomUUID(),"Electronics",true),
                true));

        assertNotNull(product);
        assertEquals("TV LCD 70 inches", product.getName());
        assertTrue(0 < product.getStock());
        assertTrue(product.isActive());

    }


    @Test
    void shouldDeactivateProduct() {
        UUID id = UUID.randomUUID();

        service.deleteById(id);

        verify(productRepository).deactivate(id);
    }
}
