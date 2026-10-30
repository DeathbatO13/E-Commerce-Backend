package com.e_commerce.cart_service.adapter.out.persistence.repository;

import com.e_commerce.cart_service.adapter.out.persistence.entity.CartJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartJpaRepository extends JpaRepository<CartJpaEntity, UUID>{

    // Los items son LAZY y open-in-view está desactivado: se cargan en la misma consulta
    // para que CartMapper pueda leerlos fuera de la sesión de Hibernate.
    @EntityGraph(attributePaths = "items")
    Optional<CartJpaEntity> findByUserId(UUID userId);

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<CartJpaEntity> findById(UUID id);

    void deleteByUserId(UUID userId);
}
