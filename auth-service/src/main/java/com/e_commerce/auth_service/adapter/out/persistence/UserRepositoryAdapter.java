package com.e_commerce.auth_service.adapter.out.persistence;


import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.out.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adaptador de salida (output adapter) que implementa el puerto {@link UserRepository}.
 * <p>
 * Conecta la interfaz del dominio (puerto) con la implementación concreta de persistencia
 * basada en Spring Data JPA ({@link UserJpaRepository}).
 * </p>
 * <p>
 * Realiza la traducción entre el modelo de dominio {@link User} y la entidad JPA
 * utilizando {@link UserMapper}.
 * </p>
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email el correo electrónico del usuario
     * @return Optional con el usuario del dominio si existe, o vacío
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    /**
     * Verifica si ya existe un usuario registrado con el correo indicado.
     *
     * @param email correo electrónico a validar
     * @return true si existe un usuario con ese email
     */
    @Override
    public boolean existByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    /**
     * Guarda o actualiza un usuario en la base de datos.
     * <p>
     * Convierte el objeto de dominio a entidad JPA, persiste y retorna
     * la versión actualizada como objeto de dominio.
     * </p>
     *
     * @param user usuario del dominio a persistir
     * @return el usuario persistido (con ID generado si era nuevo)
     */
    @Override
    public User save(User user) {
        return UserMapper.toDomain(
                jpaRepository.save(UserMapper.toEntity(user))
        );
    }
}
