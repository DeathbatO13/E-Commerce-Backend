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


@ExtendWith(MockitoExtension.class)
public class registerTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoderPort passwordEncoder;

    @Mock
    TokenGeneratorPort tokenGenerator;

    @InjectMocks
    AuthApplicationService authService;

    @Test
    void registroExitoso() {
        String email = "test@mail.com";
        String rawPassword = "password";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(rawPassword))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.register(email, rawPassword);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertTrue(user.isEnabled());

        verify(userRepository).save(any(User.class));

    }

    @Test
    void hacerInicioExitoso() {
        User user = new User(
                UUID.randomUUID(),
                "test@mail.com",
                "hashed",
                Set.of(Role.CLIENT),
                true
        );

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);
        when(tokenGenerator.generateToken(any(), any()))
                .thenReturn("fake-token");

        String token = authService.login(user.getEmail(), "password");

        assertEquals("fake-token", token);
    }

}
