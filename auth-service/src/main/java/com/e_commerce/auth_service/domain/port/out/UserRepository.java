package com.e_commerce.auth_service.domain.port.out;

import com.e_commerce.auth_service.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (output port) que define el contrato para el acceso a la persistencia
 * de usuarios desde el dominio.
 * <p>
 * Este puerto representa la abstracción del almacenamiento de usuarios en el sistema,
 * permitiendo que el dominio (casos de uso y entidades) dependa únicamente de esta interfaz
 * sin conocer detalles de implementación (JPA, MongoDB, base de datos en memoria, etc.).
 * </p>
 * <p>
 * Es implementado por adaptadores de salida en la capa de infraestructura
 * (ej: {@code UserRepositoryAdapter}).
 * </p>
 * <p>
 * Define solo las operaciones mínimas necesarias para los casos de uso de autenticación
 * y registro en esta aplicación.
 * </p>
 */
public interface UserRepository {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email el correo electrónico único del usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario registrado con el correo electrónico indicado.
     * <p>
     * Útil principalmente durante el proceso de registro para prevenir duplicados.
     * </p>
     *
     * @param email el correo electrónico a verificar
     * @return {@code true} si ya existe un usuario con ese email
     */
    boolean existByEmail(String email);

    /**
     * Guarda o actualiza un usuario en el almacenamiento persistente.
     * <p>
     * Si el usuario es nuevo (no tiene ID persistido), genera el identificador.
     * Retorna la versión actualizada/persistida del usuario.
     * </p>
     *
     * @param user el usuario del dominio a persistir o actualizar
     * @return el usuario persistido (con ID generado si era nuevo)
     */
    User save(User user);

    /**
     * Busca un usuario por su id.
     *
     * @param id el id único del usuario
     * @return Optional con el usuario encontrado o vacío si no existe
     */
    Optional<User> findById(UUID id);

    List<User> findAll();
}
