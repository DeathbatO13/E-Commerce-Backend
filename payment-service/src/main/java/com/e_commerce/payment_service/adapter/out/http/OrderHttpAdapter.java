package com.e_commerce.payment_service.adapter.out.http;

import com.e_commerce.payment_service.domain.port.out.OrderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class OrderHttpAdapter implements OrderPort {

    private final RestClient restClient;

    public OrderHttpAdapter(
            RestClient.Builder builder,
            @Value("${order.service.url:http://localhost:8084}") String orderServiceUrl) {
        this.restClient = builder.baseUrl(orderServiceUrl).build();
    }

    @Override
    public void markOrderAsPaid(UUID orderId) {
        restClient.post()
                .uri("/orders/{id}/pay", orderId)
                .retrieve()
                .toBodilessEntity();
    }
}
