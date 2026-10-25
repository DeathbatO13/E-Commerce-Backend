package com.e_commerce.order_service.adapter.out.http;

import com.e_commerce.order_service.adapter.out.http.dto.ProductDto;
import com.e_commerce.order_service.domain.ports.out.CatalogPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CatalogHttpAdapter implements CatalogPort {

    private final RestClient restClient;

    public CatalogHttpAdapter(
            RestClient.Builder builder,
            @Value("${catalog.service.url:http://localhost:8082}") String catalogServiceUrl) {
        this.restClient = builder.baseUrl(catalogServiceUrl).build();
    }

    @Override
    public BigDecimal getProductPrice(UUID productId) {
        ProductDto product = restClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .body(ProductDto.class);

        if (product == null || !product.active()) {
            throw new IllegalArgumentException("Product not found or inactive: " + productId);
        }

        return product.price();
    }
}
