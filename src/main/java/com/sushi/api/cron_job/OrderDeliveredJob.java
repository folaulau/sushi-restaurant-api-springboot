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
import com.sushi.api.entity.order.DeliveryMethod;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderRepository;
import com.sushi.api.entity.order.OrderService;
import com.sushi.api.entity.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderDeliveredJob {

  @Autowired
  private OrderRepository orderRepository;
  
  @Autowired
  private OrderService orderService;

  /*
   * every minute check for Orders that have been placed to start preping
   */
  @Scheduled(fixedRate = 1000 * 60)
  public void checkOrderDelivered() {
    log.info("checkOrderDelivered {}", LocalDateTime.now());

    int pageNumber = 0;
    int pageSize = 20;
    Pageable page = PageRequest.of(pageNumber, pageSize);
    Page<Order> result = null;

    /**
     * 4 minutes on average to deliver order
     */
    LocalDateTime deliveryTime = LocalDateTime.now().minusMinutes(4);

    while (true) {

      result = orderRepository.findByStatusAndDeliveryMethodAndDeliverStartTimeBefore(
          OrderStatus.DELIVERING, DeliveryMethod.DROP_OFF, deliveryTime, page);

      List<Order> orders = result.getContent();

      orders.stream().forEach(order -> {

        order = orderService.markOrderAsDelivered(order);

        log.info("order={}", order.toJson());
      });

      if (result.hasNext()) {
        ++pageNumber;
        page = PageRequest.of(pageNumber, pageSize);
      } else {
        break;
      }

    }
  }
}
