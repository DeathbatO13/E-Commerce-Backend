package com.e_commerce.auth_service.domain.model;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad de dominio que representa un usuario del sistema.
 * Los roles solo cambian a través de {@link #changeRole(Role)}, que aplica las reglas de negocio.
 */
public class User {

    private final UUID id;
    private final String email;
    private final String password;
    private final String fullname;
    private Set<Role> roles;
    private final boolean enabled;

    public User(UUID id, String email, String password,String fullname, Set<Role> roles, boolean enabled){
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.enabled = enabled;
        this.fullname = fullname;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname(){
        return fullname;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isSuperAdmin() {
        return roles != null && roles.contains(Role.SUPER_ADMIN);
    }

    /**
     * Reemplaza el rol del usuario.
     *
     * @throws IllegalArgumentException si se intenta asignar SUPER_ADMIN (solo existe el inicial)
     * @throws IllegalStateException    si el usuario es SUPER_ADMIN, que no puede perder su rol
     */
    public void changeRole(Role newRole) {
        if (newRole == Role.SUPER_ADMIN) {
            throw new IllegalArgumentException("SUPER_ADMIN cannot be assigned");
        }
        if (isSuperAdmin()) {
            throw new IllegalStateException("SUPER_ADMIN role cannot be changed");
        }
        this.roles = Set.of(newRole);
    }

}
