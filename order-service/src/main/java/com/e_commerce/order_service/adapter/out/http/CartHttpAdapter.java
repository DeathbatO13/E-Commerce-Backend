package com.e_commerce.order_service.adapter.out.http;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import com.e_commerce.order_service.adapter.out.http.dto.CartDto;
import com.e_commerce.order_service.domain.model.CartSnapshot;
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
    public CartSnapshot getActiveCart(UUID userId, String token) {
        CartDto cart = restClient.get()
                .uri("/cart")
                .header("Authorization", token)
                .retrieve()
                // Si cart rechaza el token, el cliente recibe 401 (antes llegaba como 502)
                .onStatus(status -> status.isSameCodeAs(HttpStatus.UNAUTHORIZED), (request, response) -> {
                    throw new BadCredentialsException("cart-service rejected the token");
                })
                .body(CartDto.class);

        if (cart == null || cart.items() == null) {
            return new CartSnapshot(userId, List.of());
        }
        return new CartSnapshot(userId, cart.items().stream()
                .map(item -> new CartSnapshot.Item(item.productId(), item.quantity()))
                .toList());
    }

    @Override
    public void clearCart(UUID userId, String token) {
        restClient.delete()
                .uri("/cart")
                .header("Authorization", token)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.UNAUTHORIZED), (request, response) -> {
                    throw new BadCredentialsException("cart-service rejected the token");
                })
                .toBodilessEntity();
    }
}
