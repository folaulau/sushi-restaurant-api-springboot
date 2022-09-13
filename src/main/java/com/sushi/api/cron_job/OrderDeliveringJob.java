package com.sushi.api.cron_job;

import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderDeliveringJob {

  /*
   * every minute check for Orders that are being delivered
   */
  @Scheduled(fixedRate = 1000 * 60)
  public void checkOrderBeingDelivered() {
      log.info("checkOrderBeingDelivered {}", LocalDateTime.now());
  }
}
