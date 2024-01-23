package com.sushi.api.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.sushi.api.exception.ApiError;
import com.sushi.api.utils.ObjectUtils;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class CustomAcccessDeniedHandler implements AccessDeniedHandler {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.info("access denied");
		response.setStatus(UNAUTHORIZED.value());
        ObjectUtils.getObjectMapper().writeValue(response.getWriter(), new ApiError(ApiError.FAILURE));
	}

}
