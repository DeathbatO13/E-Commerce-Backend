package com.e_commerce.order_service.persistence.repository;

import com.e_commerce.order_service.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByUserId(UUID userId);
}
