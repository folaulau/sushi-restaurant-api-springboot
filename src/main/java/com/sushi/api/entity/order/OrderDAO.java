package com.sushi.api.entity.order;

import java.util.Optional;

public interface OrderDAO {

  Optional<Order> findByUuid(String uuid);

  Order save(Order order);

}
