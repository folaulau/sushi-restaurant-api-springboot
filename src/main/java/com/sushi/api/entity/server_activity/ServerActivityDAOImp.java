package com.sushi.api.entity.server_activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ServerActivityDAOImp implements ServerActivityDAO {

  @Autowired
  private ServerActivityRepository serverActivityRepository;

  @Override
  public void saveAsync(ServerActivity serverActivity) {
    serverActivityRepository.saveAndFlush(serverActivity);
  }

}
