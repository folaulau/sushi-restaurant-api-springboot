package com.sushi.api.security.serveractivity;

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

public interface ServerActivityDAO {

  @Async
  void saveAsync(ServerActivity serverActivity);

  ServerActivity save(ServerActivity serverActivity);
  
  LocalDateTime getLastActivityTimestamp();

  ServerActivity getLastActivity();
  
}
