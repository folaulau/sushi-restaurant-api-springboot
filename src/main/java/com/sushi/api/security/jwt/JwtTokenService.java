package com.sushi.api.security.jwt;

import com.sushi.api.entity.user.User;

public interface JwtTokenService {
    
    public String generateUserToken(User user);

    public JwtPayload getPayloadByToken(String token);

}
