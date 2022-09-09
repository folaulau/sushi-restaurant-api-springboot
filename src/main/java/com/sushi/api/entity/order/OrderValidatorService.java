package com.sushi.api.entity.order;

import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import com.sushi.api.dto.OrderRequestDTO;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.user.User;

public interface OrderValidatorService {

  Triple<Order, User, Map<String,LineItem>> validateCreateUpdate(OrderRequestDTO orderRequestDTO);
}
