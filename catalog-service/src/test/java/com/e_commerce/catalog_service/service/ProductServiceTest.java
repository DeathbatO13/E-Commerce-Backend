package com.e_commerce.catalog_service.service;

import com.e_commerce.catalog_service.application.service.ProductApplicationService;
import com.e_commerce.catalog_service.domain.port.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductApplicationService service;

    @Test
    void shouldDeactivateProduct() {
        UUID id = UUID.randomUUID();

        service.deleteById(id);

        verify(productRepository).deactivate(id);
    }
}
