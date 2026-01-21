package com.e_commerce.catalog_service.domain.port.in;

import java.util.UUID;

public interface DeleteProductUseCase {
    void deleteById(UUID productId);
}
