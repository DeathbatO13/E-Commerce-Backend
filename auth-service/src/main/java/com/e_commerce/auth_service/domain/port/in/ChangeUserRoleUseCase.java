package com.e_commerce.auth_service.domain.port.in;

import java.util.UUID;

public interface ChangeUserRoleUseCase{

    void changeUserRole(UUID userId, String role);

}
