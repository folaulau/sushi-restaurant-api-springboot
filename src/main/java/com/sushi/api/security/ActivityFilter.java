package com.sushi.api.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import com.sushi.api.exception.ApiError;
import com.sushi.api.security.jwt.JwtPayload;
import com.sushi.api.security.jwt.JwtTokenService;
import com.sushi.api.security.serveractivity.ServerActivity;
import com.sushi.api.security.serveractivity.ServerActivityDAO;
import com.sushi.api.utils.ApiSessionUtils;
import com.sushi.api.utils.HttpUtils;
import com.sushi.api.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Component
@Order(2)
public class ActivityFilter implements Filter {

  @Autowired
  private ServerActivityDAO serverActivityDAO;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    // log.info("ActivityFilter.doFilter(..)");
    HttpServletResponse response = (HttpServletResponse) resp;
    HttpServletRequest request = (HttpServletRequest) req;

    String clientIpAddress = HttpUtils.getRequestIP(request);
    String token = request.getHeader("token");

    log.debug("token: {}, ip address", token, clientIpAddress);
    log.debug("url: {}", HttpUtils.getFullURL(request));

    String path = HttpUtils.getURLPath(request);

    if (!Arrays.asList("/v1/ping").contains(path)) {
      serverActivityDAO
          .saveAsync(new ServerActivity(null, path, clientIpAddress, LocalDateTime.now()));
    }


    if (token != null && !token.isEmpty()) {

      JwtPayload jwtPayload = jwtTokenService.getPayloadByToken(token);

      if (jwtPayload == null || jwtPayload.getExp().before(new Date())) {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());

        String message = "Invalid token in header";
        log.debug("Error message: {}, context path: {}, url: {}", message, request.getContextPath(),
            request.getRequestURI());

        ObjectUtils.getObjectMapper().writeValue(response.getWriter(),
            new ApiError(UNAUTHORIZED, "Access Denied", Collections.singletonList(message)));

        return;
      }

      ApiSessionUtils.setSessionToken(jwtPayload);

      ThreadContext.put("userUuid", jwtPayload.getSub());

    }

    chain.doFilter(req, resp);

    ThreadContext.clearMap();
  }
}
