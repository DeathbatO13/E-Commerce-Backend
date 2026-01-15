package com.e_commerce.auth_service.adapter.in.rest;

import com.e_commerce.auth_service.adapter.in.rest.dto.LoginRequest;
import com.e_commerce.auth_service.adapter.in.rest.dto.LoginResponse;
import com.e_commerce.auth_service.adapter.in.rest.dto.RegisterRequest;
import com.e_commerce.auth_service.adapter.in.rest.dto.RegisterResponse;
import com.e_commerce.auth_service.domain.model.User;
import com.e_commerce.auth_service.domain.port.in.LoginUseCase;
import com.e_commerce.auth_service.domain.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController{

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase){
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody @Valid RegisterRequest request){
        User user = registerUserUseCase.register(
                request.email(),
                request.password()
        );

        return new RegisterResponse(
            user.getId(),
            user.getEmail()
        );
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        String token = loginUseCase.login(
                request.email(),
                request.password()
        );

        return new LoginResponse(token);
    }
}
