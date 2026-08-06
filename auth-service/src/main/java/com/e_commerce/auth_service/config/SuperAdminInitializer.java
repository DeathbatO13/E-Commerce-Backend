package com.e_commerce.auth_service.config;

import com.e_commerce.auth_service.domain.model.Role;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.out.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class SuperAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String email;

    @Value("{admin.password}")
    private String password;

    @Value("${admin.fullname}")
    private String fullname;

    public SuperAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userRepository.findByEmail(email).orElseGet(() -> {
            User superAdmin = new User(
                    UUID.randomUUID(),
                    fullname,
                    email,
                    passwordEncoder.encode(password),
                    Set.of(Role.SUPER_ADMIN),
                    true
            );
            return userRepository.save(superAdmin);
        });
    }
}