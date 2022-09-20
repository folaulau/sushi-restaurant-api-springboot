package com.sushi.api.entity.server_activity;

import org.springframework.scheduling.annotation.Async;

public interface ServerActivityDAO {

  @Async
  void saveAsync(ServerActivity serverActivity);
  
}
