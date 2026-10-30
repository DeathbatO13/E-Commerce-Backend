package com.e_commerce.auth_service.application.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import com.e_commerce.auth_service.domain.exception.ResourceNotFoundException;
import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.ChangeUserRoleUseCase;
import com.e_commerce.auth_service.domain.port.in.ListUsersUseCase;
import com.e_commerce.auth_service.domain.port.in.LoginUseCase;
import com.e_commerce.auth_service.domain.port.in.RegisterUserUseCase;
import com.e_commerce.auth_service.domain.port.out.PasswordEncoderPort;
import com.e_commerce.auth_service.domain.port.out.TokenGeneratorPort;
import com.e_commerce.auth_service.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio de aplicación que implementa los casos de uso principales de autenticación:
 * registro de usuarios y login con generación de token JWT.
 * <p>
 * Actúa como orquestador entre los puertos de entrada (casos de uso) y los puertos de salida
 * (repositorio, codificador de contraseñas y generador de tokens).
 * </p>
 * <p>
 * Todas las operaciones son transaccionales para garantizar consistencia en la base de datos.
 * </p>
 */
@Service
@Transactional
public class AuthApplicationService implements LoginUseCase,
        RegisterUserUseCase, ListUsersUseCase, ChangeUserRoleUseCase {

    UserRepository userRepository;
    PasswordEncoderPort passwordEncoder;
    TokenGeneratorPort tokenGenerator;

    public AuthApplicationService(UserRepository userRepository, PasswordEncoderPort passwordEncoder,
                                  TokenGeneratorPort tokenGenerator){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * Autentica a un usuario mediante email y contraseña.
     * <p>
     * Verifica la existencia del usuario y la validez de la contraseña.
     * Si las credenciales son correctas, genera y retorna un token JWT.
     * </p>
     *
     * @param email        correo electrónico del usuario
     * @param rawPassword  contraseña en texto plano proporcionada por el cliente
     * @return token JWT válido para autenticación en la API
     * @throws BadCredentialsException si las credenciales son inválidas (401)
     * @throws DisabledException       si el usuario está deshabilitado (401)
     */
    @Override
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Se verifica después de la contraseña para no revelar qué cuentas existen
        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        return tokenGenerator.generateToken(user);

    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Valida que el email no esté ya registrado, codifica la contraseña,
     * asigna el rol por defecto (CLIENT) y persiste el usuario.
     * </p>
     *
     * @param email       correo electrónico (debe ser único)
     * @param rawPassword contraseña en texto plano
     * @param fullname    nombre completo del usuario
     * @return el usuario recién creado y persistido
     * @throws IllegalStateException si el email ya está registrado (409)
     */
    @Override
    public User register(String email, String rawPassword, String fullname){
        if (userRepository.existByEmail(email)) {
            throw new IllegalStateException("Email already registered");
        }

        User user = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(rawPassword),
                fullname,
                Set.of(Role.CLIENT),
                true
        );

        return userRepository.save(user);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }


    @Override
    public void changeUserRole(UUID userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role newRole;
        try {
            newRole = Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role");
        }

        user.changeRole(newRole);
        userRepository.save(user);
    }
}

