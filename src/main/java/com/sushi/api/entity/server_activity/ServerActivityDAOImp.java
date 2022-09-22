package com.sushi.api.entity.server_activity;

import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ServerActivityDAOImp implements ServerActivityDAO {

  @Autowired
  private ServerActivityRepository serverActivityRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public void saveAsync(ServerActivity serverActivity) {
    serverActivityRepository.saveAndFlush(serverActivity);
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


}
