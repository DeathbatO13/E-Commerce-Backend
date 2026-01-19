package com.e_commerce.catalog_service.domain.port.in;

import com.e_commerce.catalog_service.domain.model.Category;

public interface CreateCategoryUseCase {
    Category create(Category category);
}
