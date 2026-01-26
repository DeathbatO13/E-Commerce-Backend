package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Category;

import java.util.UUID;

public interface UpdateCategoryUseCase {
    Category update (UUID uuid, String name);
}
