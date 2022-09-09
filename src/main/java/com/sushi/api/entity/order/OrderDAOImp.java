package com.sushi.api.entity.order;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OrderDAOImp implements OrderDAO {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Order> findByUuid(String uuid) {
    return orderRepository.findByUuid(uuid);
  }

  @Override
  public Order save(Order order) {
    return orderRepository.saveAndFlush(order);
  }
  
}
