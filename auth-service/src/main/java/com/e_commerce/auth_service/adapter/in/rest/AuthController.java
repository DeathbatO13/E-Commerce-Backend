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


/**
 * Controlador REST que expone los endpoints públicos de autenticación:
 * registro de nuevos usuarios y login para obtener token JWT.
 * <p>
 * Actúa como adaptador de entrada (input adapter) en la arquitectura hexagonal,
 * traduciendo solicitudes HTTP a llamadas a los casos de uso del dominio.
 * </p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController{

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    /**
     * Constructor con inyección de dependencias de los casos de uso.
     *
     * @param registerUserUseCase caso de uso para registrar usuarios
     * @param loginUseCase        caso de uso para autenticar usuarios
     */
    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase){
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Valida los datos de entrada y delega la lógica de negocio al caso de uso correspondiente.
     * Retorna HTTP 201 Created cuando el registro es exitoso.
     * </p>
     *
     * @param request DTO con email, contraseña y nombre completo
     * @return respuesta con ID y email del usuario recién creado
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request){
        User user = registerUserUseCase.register(
                request.email(),
                request.password(),
                request.fullname()
        );

        return new RegisterResponse(
            user.getId(),
            user.getEmail()
        );
    }

    /**
     * Autentica a un usuario y genera un token JWT.
     * <p>
     * Valida las credenciales y, si son correctas, retorna un token válido.
     * En caso de credenciales inválidas, el caso de uso lanzará una excepción
     * que será manejada por un controlador de excepciones global.
     * </p>
     *
     * @param request DTO con email y contraseña
     * @return respuesta que contiene el token JWT generado
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        String token = loginUseCase.login(
                request.email(),
                request.password()
        );

        return new LoginResponse(token, "Bearer", 3600);
    }
}
