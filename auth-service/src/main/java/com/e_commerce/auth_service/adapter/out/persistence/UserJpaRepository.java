package com.e_commerce.auth_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz del repositorio Spring Data JPA para usuarios.
 * <p>
 * Extiende {@link JpaRepository} para operaciones estándar (findByEmail, existsByEmail)
 * y añade consultas específicas necesarias en el flujo de autenticación y registro.
 * </p>
 */
interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findById(UUID id);

    List<UserJpaEntity> findAll();
}
