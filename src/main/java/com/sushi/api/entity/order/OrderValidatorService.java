package com.sushi.api.entity.order;

import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.util.Pair;
import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.LineItemCreateDTO;
import com.sushi.api.dto.OrderConfirmDTO;
import com.sushi.api.dto.OrderRemoveRequestDTO;
import com.sushi.api.dto.OrderRequestDTO;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.user.User;

public interface OrderValidatorService {

  Triple<Order, User, LineItemCreateDTO> validateCreateUpdate(OrderRequestDTO orderRequestDTO);
  
  Pair<Order, LineItem> validateRemoval(OrderRemoveRequestDTO orderRemoveRequestDTO);

  Pair<Order, PaymentIntent> validatePayment(OrderConfirmDTO orderConfirmDTO);

  Order validateGetByUuid(String uuid);  
}
