package com.e_commerce.catalog_service.adapter.in.rest.dto.response;

import java.util.UUID;

public record CategoryResponse(UUID id, String name, boolean active) {
}
