package com.e_commerce.order_service.adapter.config;

import com.e_commerce.order_service.adapter.out.persistence.OrderAdapter;
import com.e_commerce.order_service.adapter.out.persistence.repository.OrderRepository;
import com.e_commerce.order_service.application.service.OrderService;
import com.e_commerce.order_service.domain.ports.out.CartPort;
import com.e_commerce.order_service.domain.ports.out.CatalogPort;
import com.e_commerce.order_service.domain.ports.out.OrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public OrderRepositoryPort orderRepositoryPort(OrderRepository repository) {
        return new OrderAdapter(repository);
    }

    /**
     * Único bean del servicio de aplicación. Implementa los puertos de entrada
     * (CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase, CancelOrderUseCase),
     * así que Spring lo inyecta por cualquiera de esas interfaces. No registrar
     * alias por puerto: serían la misma instancia y Spring vería dos beans OrderService.
     */
    @Bean
    public OrderService orderService(
            OrderRepositoryPort repositoryPort,
            CartPort cartPort,
            CatalogPort catalogPort) {
        return new OrderService(repositoryPort, cartPort, catalogPort);
    }

}
