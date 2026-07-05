package com.e_commerce.auth_service.loginAndRegisterTest;


import com.e_commerce.auth_service.application.service.AuthApplicationService;
import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.out.PasswordEncoderPort;
import com.e_commerce.auth_service.domain.port.out.TokenGeneratorPort;
import com.e_commerce.auth_service.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para los casos de uso principales del servicio de autenticación
 * {@link AuthApplicationService}: registro y login.
 * <p>
 * Se utiliza Mockito para simular los puertos de salida del dominio y aislar
 * completamente las pruebas del servicio de aplicación.
 * </p>
 * <p>
 * Cubre los flujos felices (happy path) de:
 * <ul>
 *   <li>Registro exitoso de un nuevo usuario</li>
 *   <li>Inicio de sesión exitoso con credenciales válidas</li>
 * </ul>
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class RegisterTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoderPort passwordEncoder;

    @Mock
    TokenGeneratorPort tokenGenerator;

    @InjectMocks
    AuthApplicationService authService;

    /**
     * Verifica el flujo completo de registro exitoso:
     * - Codificación de la contraseña
     * - Persistencia del usuario con rol por defecto
     * - Retorno de la entidad persistida
     * - Llamada correcta al repositorio
     */
    @Test
    void registroExitoso() {
        String email = "test@mail.com";
        String rawPassword = "password";
        String fullname = "Administrator";

        when(passwordEncoder.encode(rawPassword))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.register(email, rawPassword, fullname);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertTrue(user.isEnabled());

        verify(userRepository).save(any(User.class));

    }

    /**
     * Verifica el flujo completo de inicio de sesión exitoso:
     * - Búsqueda del usuario por email
     * - Verificación correcta de la contraseña
     * - Generación del token
     * - Retorno del token generado
     */
    @Test
    void hacerInicioExitoso() {
        User user = new User(
                UUID.randomUUID(),
                "test@mail.com",
                "hashed",
                "Administrator",
                Set.of(Role.CLIENT),
                true
        );

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);
        when(tokenGenerator.generateToken(any()))
                .thenReturn("fake-token");

        String token = authService.login(user.getEmail(), "password");

        assertEquals("fake-token", token);
    }

}
