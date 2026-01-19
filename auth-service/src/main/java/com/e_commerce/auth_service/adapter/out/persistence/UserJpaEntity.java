package com.e_commerce.auth_service.adapter.out.persistence;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Entidad JPA persistente para usuarios.
 * <p>
 * Representa la tabla <b>users</b> y es utilizada por Spring Data JPA.
 * Sirve como puente entre el puerto de salida (UserRepositoryPort) y la base de datos.
 * </p>
 * <p>
 * Campos sensibles como {@code password} deben manejarse siempre hasheados.
 * Los roles se cargan de forma EAGER para facilitar la autenticación con JWT/Spring Security.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity{

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullname;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<String> roles;

    private boolean enabled;
}
