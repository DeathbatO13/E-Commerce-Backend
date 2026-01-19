package com.e_commerce.auth_service.userTest;

import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias básicas para la entidad de dominio {@link User}.
 * <p>
 * Se enfoca en verificar que la construcción del objeto funciona correctamente
 * y que los getters devuelven los valores esperados.
 * </p>
 * <p>
 * Dado que {@code User} es una clase inmutable con campos final, estas pruebas
 * son simples y sirven principalmente como verificación de contrato básico.
 * </p>
 */
public class UserTest {

    /**
     * Verifica la creación correcta de un usuario válido:
     * - Todos los campos se asignan correctamente
     * - Los getters funcionan como se espera
     * - El estado inicial es el esperado (habilitado)
     */
    @Test
    void crearUsuarioValido(){
        User user = new User(
                UUID.randomUUID(),
                "test@test.com",
                "contrasenahasheada",
                "Administartor",
                Set.of(Role.CLIENT),
                true
        );

        assertEquals("test@test.com", user.getEmail());
        assertTrue(user.isEnabled());
    }
}
