package com.sushi.api.security;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import com.sushi.api.exception.ApiError;
import com.sushi.api.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("logout(..)");

        String token = request.getParameter("token");

        boolean loggedOut = authenticationService.logOutUser(token);

        log.debug("loggedOut={}", loggedOut);

        if (loggedOut == false) {
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.BAD_REQUEST.value());

                String message = "Token not found";
                log.debug("Error message: {}", message);

                ObjectUtils.getObjectMapper()
                        .writeValue(response.getWriter(), new ApiError(HttpStatus.BAD_REQUEST, message, "Session not found for requested token", "Token might have been logged out already"));

                return;
            } catch (IOException e) {
                log.info("IOException, msg={}", e.getLocalizedMessage());
            }
        }
    }

}
