package com.sushi.api.security;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.EntityDTOMapper;
import com.sushi.api.entity.user.User;
import com.sushi.api.exception.ApiError;
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
    private EntityDTOMapper     entityMapper;

    @Autowired
    private JwtTokenService     jwtTokenService;

    @Override
    public AuthenticationResponseDTO authenticate(User user) {
        String jwt = jwtTokenService.generateUserToken(user);

        AuthenticationResponseDTO auth = entityMapper.mapUserToAuthenticationResponse(user);
        auth.setToken(jwt);
        auth.setRole(user.getRoleAsString());

        return auth;
    }

    @Override
    public boolean authorizeRequest(String token, JwtPayload jwtPayload) {

        log.debug("jwtPayload={}", ObjectUtils.toJson(jwtPayload));

        if (jwtPayload == null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(UNAUTHORIZED.value());

            String message = "Invalid token in header";
            log.debug("Error message: {}, context path: {}, url: {}", message, request.getContextPath(), request.getRequestURI());

            try {
                ObjectUtils.getObjectMapper().writeValue(response.getWriter(), new ApiError(UNAUTHORIZED, "Access Denied", message, "Unable to verify token"));
            } catch (IOException e) {
                log.warn("IOException, msg={}", e.getLocalizedMessage());
            }

            return false;
        }

        ApiSessionUtils.setSessionToken(new WebAuthenticationDetails(request), jwtPayload);

        return true;
    }

    @Override
    public boolean logOutUser(String token) {
        // TODO Auto-generated method stub
        return false;
    }

}
