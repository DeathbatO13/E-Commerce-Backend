package com.e_commerce.payment_service.adapter.out.http;

import com.e_commerce.payment_service.domain.port.out.OrderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class OrderHttpAdapter implements OrderPort {


    static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final RestClient restClient;
    private final String internalToken;

    public OrderHttpAdapter(
            RestClient.Builder builder,
            @Value("${order.service.url:http://localhost:8084}") String orderServiceUrl,
            @Value("${security.internal-token}") String internalToken) {
        this.restClient = builder.baseUrl(orderServiceUrl).build();
        this.internalToken = internalToken;

    private final RestClient restClient;

    public OrderHttpAdapter(
            RestClient.Builder builder,
            @Value("${order.service.url:http://localhost:8084}") String orderServiceUrl) {
        this.restClient = builder.baseUrl(orderServiceUrl).build();

    }

    @Override
    public void markOrderAsPaid(UUID orderId) {

        // order-service solo acepta /pay de otros servicios, autenticados con el token interno
        restClient.post()
                .uri("/orders/{id}/pay", orderId)
                .header(INTERNAL_TOKEN_HEADER, internalToken)

        restClient.post()
                .uri("/orders/{id}/pay", orderId)

                .retrieve()
                .toBodilessEntity();
    }
}
