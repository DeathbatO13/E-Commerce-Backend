package com.e_commerce.auth_service.application.service;

import com.e_commerce.auth_service.config.JwtProvider;
import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.LoginUseCase;
import com.e_commerce.auth_service.domain.port.in.RegisterUserUseCase;
import com.e_commerce.auth_service.domain.port.out.PasswordEncoderPort;
import com.e_commerce.auth_service.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;


@Service
@Transactional
public class AuthApplicationService implements LoginUseCase, RegisterUserUseCase{

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthApplicationService(UserRepository userRepository, PasswordEncoderPort passwordEncoder,
                                  JwtProvider jwtProvider){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }


    @Override
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtProvider.generarToken(user);

    }

    @Override
    public User register(String email, String rawPassword){
        if (userRepository.existByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(rawPassword),
                Set.of(Role.CLIENT),
                true
        );

        return userRepository.save(user);
    }
}
