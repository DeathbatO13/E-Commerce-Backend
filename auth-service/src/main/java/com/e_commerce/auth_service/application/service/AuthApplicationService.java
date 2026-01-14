package com.e_commerce.auth_service.application.service;

import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.LoginUseCase;
import com.e_commerce.auth_service.domain.port.in.RegisterUserUseCase;
import com.e_commerce.auth_service.domain.port.out.PasswordEncoderPort;
import com.e_commerce.auth_service.domain.port.out.TokenGeneratorPort;
import com.e_commerce.auth_service.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;


@Service
public class AuthApplicationService implements LoginUseCase, RegisterUserUseCase{

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    public AuthApplicationService(UserRepository userRepository, PasswordEncoderPort passwordEncoder,
                                  TokenGeneratorPort tokenGenerator){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }


    @Override
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Invalid credentials"));

        if(!passwordEncoder.matches(rawPassword, user.getPassword()))
            throw new IllegalArgumentException("Invalid credentials");

        return tokenGenerator.generateToken(
                user.getId().toString(),
                user.getEmail()
        );
    }

    @Override
    public User register(String email, String rawPassword){
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("User already exists");
        });

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(
                UUID.randomUUID(),
                email,
                encodedPassword,
                Set.of(Role.CLIENT),
                true
        );

        return userRepository.save(user);
    }
}
