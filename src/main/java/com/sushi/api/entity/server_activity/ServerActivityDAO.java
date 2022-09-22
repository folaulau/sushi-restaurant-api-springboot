package com.sushi.api.entity.server_activity;

import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Async;

public interface ServerActivityDAO {

  @Async
  void saveAsync(ServerActivity serverActivity);
  
  LocalDateTime getLastActivityTimestamp();
  
}
