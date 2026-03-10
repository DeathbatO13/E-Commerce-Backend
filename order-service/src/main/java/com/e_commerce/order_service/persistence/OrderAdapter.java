package com.e_commerce.order_service.persistence;

import com.e_commerce.order_service.domain.model.Order;
import com.e_commerce.order_service.domain.ports.out.OrderRepositoryPort;
import com.e_commerce.order_service.persistence.mapper.OrderMapper;
import com.e_commerce.order_service.persistence.repository.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderAdapter implements OrderRepositoryPort {

    private final OrderRepository repository;

    public OrderAdapter(OrderRepository repository){
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        var entity = OrderMapper.toEntity(order);
        var saved = repository.save(entity);
        return OrderMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id)
                .map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll()
                .stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }
}
