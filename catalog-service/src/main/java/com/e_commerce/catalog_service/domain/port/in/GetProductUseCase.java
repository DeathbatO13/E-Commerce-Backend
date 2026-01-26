package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Product;

import java.util.UUID;

public interface GetProductUseCase {
    Product getById(UUID uuid);
}
