package com.sushi.api.entity.order.calculator;

import com.sushi.api.dto.AddressCreateUpdateDTO;
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.order.DeliveryMethod;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderCostDetails;

public interface OrderCalculatorService {

  OrderCostDetails calculateOrderTotalCost(Order order, DeliveryMethod deliveryMethod, AddressCreateUpdateDTO address);
}
