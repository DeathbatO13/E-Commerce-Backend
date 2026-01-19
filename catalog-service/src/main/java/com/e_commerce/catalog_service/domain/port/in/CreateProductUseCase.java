package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

public interface CreateProductUseCase {
    Product create(Product product);

}
