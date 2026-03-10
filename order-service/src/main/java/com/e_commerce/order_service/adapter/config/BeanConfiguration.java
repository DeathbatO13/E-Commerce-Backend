package com.e_commerce.order_service.adapter.config;


import com.e_commerce.order_service.adapter.out.persistence.OrderAdapter;
import com.e_commerce.order_service.adapter.out.persistence.repository.OrderRepository;
import com.e_commerce.order_service.application.service.OrderService;
import com.e_commerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.e_commerce.order_service.domain.ports.in.GetOrderUseCase;
import com.e_commerce.order_service.domain.ports.out.OrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration{

    @Bean
    public OrderRepositoryPort orderRepositoryPort(OrderRepository repository){
        return new OrderAdapter(repository);
    }

    @Bean
    public OrderService orderService(OrderRepositoryPort repositoryPort){
        return new OrderService(repositoryPort);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderService service){
        return service;
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderService service){
        return service;
    }

}
