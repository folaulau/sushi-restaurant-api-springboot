package com.sushi.api.entity.order;

import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sushi.api.dto.OrderConfirmDTO;
import com.sushi.api.dto.OrderDTO;
import com.sushi.api.dto.OrderRemoveRequestDTO;
import com.sushi.api.dto.OrderRequestDTO;

public interface OrderService {

  OrderDTO createUpdateOrder(@Valid OrderRequestDTO orderRequestDTO);

  OrderDTO getByUuid(String uuid);

  OrderDTO removeAll(String uuid);

  OrderDTO remove(OrderRemoveRequestDTO orderRemoveRequestDTO);

  OrderDTO confirmGuestPayment(OrderConfirmDTO orderConfirmDTO);

  Order prepareOrder(String uuid);
  Order prepareOrder(Order order);
  
  Order markOrderAsReadyForPickUp(String uuid);
  Order markOrderAsReadyForPickUp(Order order);
  
  Order markOrderAsDelivering(String uuid);
  Order markOrderAsDelivering(Order order);
  
  Order markOrderAsPickedUp(String uuid);
  Order markOrderAsPickedUp(Order order);
  
  Order markOrderAsDelivered(String uuid);
  Order markOrderAsDelivered(Order order);
}
