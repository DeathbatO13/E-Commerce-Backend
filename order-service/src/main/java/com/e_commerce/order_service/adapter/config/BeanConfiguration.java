package com.e_commerce.order_service.adapter.config;

import com.e_commerce.order_service.adapter.out.persistence.OrderAdapter;
import com.e_commerce.order_service.adapter.out.persistence.repository.OrderRepository;
import com.e_commerce.order_service.application.service.OrderService;
import com.e_commerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.PayOrderUseCase;
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

    @Bean
    public OrderService orderService(
            OrderRepositoryPort repositoryPort,
            CartPort cartPort,
            CatalogPort catalogPort) {
        return new OrderService(repositoryPort, cartPort, catalogPort);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderService service) {
        return service;
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderService service) {
        return service;
    }

    @Bean
    public PayOrderUseCase payOrderUseCase(OrderService service) {
        return service;
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(OrderService service) {
        return service;
    }

}
