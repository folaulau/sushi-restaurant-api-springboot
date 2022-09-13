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
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderDAO;
import com.sushi.api.entity.order.OrderRepository;
import com.sushi.api.entity.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderPrepJob {

  @Autowired
  private OrderRepository orderRepository;

  /*
   * every minute check for Orders that have been placed to start preping
   */
  @Scheduled(fixedRate = 1000 * 60)
  public void checkOrderPrep() {
    log.info("checkOrderPrep {}", LocalDateTime.now());

    int pageNumber = 0;
    int pageSize = 20;
    Pageable page = PageRequest.of(pageNumber, pageSize);

    Page<Order> result = orderRepository.findByStatusAndPaidAtBefore(OrderStatus.ORDER_PLACED,
        LocalDateTime.now(), page);

    while (result != null && result.hasContent()) {

      List<Order> orders = result.getContent();

      orders.stream().map(order -> {
        order.setStatus(OrderStatus.PREPARING_ORDER);
        orderRepository.saveAndFlush(order);
        return order;
      }).collect(Collectors.toList());

      if (result.hasNext()) {
        ++pageNumber;
        page = PageRequest.of(pageNumber, pageSize);
      } else {
        break;
      }

      result = orderRepository.findByStatusAndPaidAtBefore(OrderStatus.ORDER_PLACED,
          LocalDateTime.now(), page);

    }
  }
}
