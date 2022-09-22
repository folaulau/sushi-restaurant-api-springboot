package com.sushi.api.cron_job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import com.amazonaws.services.ecs.model.UpdateServiceResult;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.StopDBInstanceRequest;
import com.sushi.api.entity.order.DeliveryMethod;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderRepository;
import com.sushi.api.entity.order.OrderService;
import com.sushi.api.entity.order.OrderStatus;
import com.sushi.api.entity.server_activity.ServerActivityDAO;
import lombok.extern.slf4j.Slf4j;

@Profile(value = {"local", "prod"})
@Component
@Slf4j
public class ShutDownServerJob {

  @Autowired
  private AmazonECS amazonECS;

  @Autowired
  private AmazonRDS amazonRDS;

  @Autowired
  private ServerActivityDAO serverActivityDAO;

  /*
   * shut down ecs server when server is inactive for 15 minutes
   */
  @Scheduled(fixedRate = 1000 * 60 * 15)
  public void checkForLastRestCall() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    log.info("checkForLastRestCall {}", LocalDateTime.now());

    LocalDateTime lastActivityTimestamp = serverActivityDAO.getLastActivityTimestamp();

    log.info("lastActivityTimestamp={}", lastActivityTimestamp);

    lastActivityTimestamp = lastActivityTimestamp.plusMinutes(15);

    log.info("15 + lastActivityTimestamp={}", lastActivityTimestamp);

    LocalDateTime now = LocalDateTime.now();

    if (lastActivityTimestamp.isBefore(now)) {
      log.info("turn off service");

      turnOffECS();
      
      turnOffRDS();

    } else {
      log.info("app is being used");
    }

  }



  private void turnOffRDS() {
    try {
      StopDBInstanceRequest stopDBInstanceRequest = new StopDBInstanceRequest();
      stopDBInstanceRequest.setDBInstanceIdentifier("sushi-api-prod");
      DBInstance dbInstance = amazonRDS.stopDBInstance(stopDBInstanceRequest);
      log.info("DBInstance:{}", dbInstance.toString());
    } catch (Exception e) {
      log.info("turnOffRDS Exception: {}", e.getLocalizedMessage());
    }

  }

  private void turnOffECS() {

    try {
      UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest();

      updateServiceRequest.setCluster("pocsoft");
      updateServiceRequest.setDesiredCount(0);
      updateServiceRequest.setService("sushi-api");

      UpdateServiceResult result = amazonECS.updateService(updateServiceRequest);

      log.info("UpdateServiceResult:{}", result.toString());
    } catch (Exception e) {
      log.info("turnOffECS Exception: {}", e.getLocalizedMessage());
    }

  }
}
