package com.e_commerce.order_service.adapter.out.http;

import com.e_commerce.order_service.adapter.out.http.dto.CartDto;
import com.e_commerce.order_service.domain.ports.out.CartPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class CartHttpAdapter implements CartPort {

    private final RestClient restClient;

    public CartHttpAdapter(
            RestClient.Builder builder,
            @Value("${cart.service.url:http://localhost:8083}") String cartServiceUrl) {
        this.restClient = builder.baseUrl(cartServiceUrl).build();
    }

    @Override
    public CartDto getActiveCart(UUID userId, String token) {
        return restClient.get()
                .uri("/cart")
                .header("Authorization", token)
                .retrieve()
                .body(CartDto.class);
    }

    @Override
    public void clearCart(UUID userId, String token) {
        restClient.delete()
                .uri("/cart")
                .header("Authorization", token)
                .retrieve()
                .toBodilessEntity();
    }
}
