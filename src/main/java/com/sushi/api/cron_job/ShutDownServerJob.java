package com.sushi.api.cron_job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import com.amazonaws.services.ecs.model.UpdateServiceResult;
import com.sushi.api.entity.order.DeliveryMethod;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderRepository;
import com.sushi.api.entity.order.OrderService;
import com.sushi.api.entity.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ShutDownServerJob {

  @Autowired
  private AmazonECS amazonECS;

  /*
   * shut down ecs server when server is inactive for 10 minutes
   */
  @Scheduled(fixedRate = 1000 * 60)
  public void checkForLastRestCall() {
    log.info("checkForLastRestCall {}", LocalDateTime.now());

    UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest();
    
    updateServiceRequest.setCluster("pocsoft");
    updateServiceRequest.setDesiredCount(0);
    updateServiceRequest.setService("sushi-api");

    UpdateServiceResult result = amazonECS.updateService(updateServiceRequest);
    
//    log.info("result:{}", result.toString());

  }
}
