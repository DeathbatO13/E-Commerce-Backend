package com.e_commerce.cart_service.adapter.out.persistence.repository;

import com.e_commerce.cart_service.adapter.out.persistence.entity.CartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartJpaRepository extends JpaRepository<CartJpaEntity, UUID>{

    Optional<CartJpaEntity> findByUserId(UUID userId);

    void deleteById(UUID id);
}
