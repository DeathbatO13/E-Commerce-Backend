package com.e_commerce.catalog_service.domain.port.in;

import java.util.UUID;

public interface DeleteCategoryUseCase{

    void deleteById(UUID categoryId);
}
