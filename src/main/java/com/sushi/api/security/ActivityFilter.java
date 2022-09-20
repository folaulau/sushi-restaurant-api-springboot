package com.sushi.api.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.TimeZone;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.sushi.api.entity.server_activity.ServerActivity;
import com.sushi.api.entity.server_activity.ServerActivityDAO;
import com.sushi.api.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(2)
public class ActivityFilter implements Filter {

  @Autowired
  private ServerActivityDAO serverActivityDAO;

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    // log.info("ActivityFilter.doFilter(..)");
    // HttpServletResponse response = (HttpServletResponse) resp;
    HttpServletRequest request = (HttpServletRequest) req;

    String clientIpAddress = HttpUtils.getRequestIP(request);
    String token = request.getHeader("token");

    String path = HttpUtils.getURLPath(request);

//    log.debug("token: {}, ip address={}", token, clientIpAddress);
//    log.debug("path: {}", path);
    
    if(!Arrays.asList("/v1/ping").contains(path)) {
      serverActivityDAO
      .saveAsync(new ServerActivity(null, path, clientIpAddress, LocalDateTime.now()));
    }

    chain.doFilter(req, resp);
  }
}
