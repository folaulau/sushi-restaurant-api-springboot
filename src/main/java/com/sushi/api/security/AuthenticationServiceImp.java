package com.sushi.api.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.EntityDTOMapper;
import com.sushi.api.entity.user.User;
import com.sushi.api.security.jwt.JwtPayload;
import com.sushi.api.security.jwt.JwtTokenService;
import com.sushi.api.utils.ApiSessionUtils;
import com.sushi.api.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationServiceImp implements AuthenticationService {

    @Autowired
    private HttpServletRequest  request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private EntityDTOMapper     entityDTOMapper;

    @Autowired
    private JwtTokenService     jwtTokenService;

    @Override
    public AuthenticationResponseDTO authenticate(User user) {
        
        String jwt = jwtTokenService.generateUserToken(user);

        AuthenticationResponseDTO auth = entityDTOMapper.mapUserToAuthenticationResponse(user);
        auth.setToken(jwt);
        auth.setRole(user.getRoleAsString());

        return auth;
    }

    @Override
    public boolean authorizeRequest(String token, JwtPayload jwtPayload) {

        log.debug("jwtPayload={}", ObjectUtils.toJson(jwtPayload));

        ApiSessionUtils.setSessionToken(jwtPayload);

        return true;
    }

    @Override
    public boolean logOutUser(String token) {
        // TODO Auto-generated method stub
        return false;
    }

}
