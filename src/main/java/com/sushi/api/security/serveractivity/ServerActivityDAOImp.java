package com.sushi.api.security.serveractivity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class ServerActivityDAOImp implements ServerActivityDAO {

  @Autowired
  private ServerActivityRepository serverActivityRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void saveAsync(ServerActivity serverActivity) {
    ServerActivity saveServerActivity = serverActivityRepository.saveAndFlush(serverActivity);
    log.info("saveServerActivity id={}", saveServerActivity.getId());
  }

  @Override
  public ServerActivity save(ServerActivity serverActivity) {
    return serverActivityRepository.saveAndFlush(serverActivity);
  }

  @Override
  public LocalDateTime getLastActivityTimestamp() {

    StringBuilder query =
        new StringBuilder("SELECT created_at FROM server_activities ORDER BY id DESC LIMIT 1");

    LocalDateTime lastActivityTimestamp = null;
    try {
      lastActivityTimestamp = jdbcTemplate.queryForObject(query.toString(), LocalDateTime.class);
    } catch (Exception e) {
    }
    return lastActivityTimestamp;
  }

  @Override
  public ServerActivity getLastActivity() {
    return serverActivityRepository.findTopByOrderByIdDesc();
  }


}
