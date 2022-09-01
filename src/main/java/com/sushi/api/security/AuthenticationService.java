package com.sushi.api.security;

import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.entity.user.User;
import com.sushi.api.security.jwt.JwtPayload;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(User user);

    boolean authorizeRequest(String token, JwtPayload jwtPayload);

    boolean logOutUser(String token);
}
