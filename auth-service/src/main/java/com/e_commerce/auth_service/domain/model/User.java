package com.e_commerce.auth_service.domain.model;

import java.util.Set;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String password;
    private final String fullname;
    private final Set<Role> roles;
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
}
