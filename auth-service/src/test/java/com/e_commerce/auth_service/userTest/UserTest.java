package com.e_commerce.auth_service.userTest;

import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    @Test
    void crearUsuarioValido(){
        User user = new User(
                UUID.randomUUID(),
                "test@test.com",
                "contrasenahasheada",
                Set.of(Role.CLIENT),
                true
        );

        assertEquals("test@test.com", user.getEmail());
        assertTrue(user.isEnabled());
    }
}
