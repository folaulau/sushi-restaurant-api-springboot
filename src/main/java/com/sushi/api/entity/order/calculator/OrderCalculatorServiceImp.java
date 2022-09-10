package com.sushi.api.entity.order.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sushi.api.dto.AddressCreateUpdateDTO;
import com.sushi.api.entity.order.DeliveryMethod;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderCostDetails;
import com.sushi.api.utils.MathUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderCalculatorServiceImp implements OrderCalculatorService {

  @Value("${spring.profiles.active}")
  private String env;

  @Value("${service.fee:2}")
  private Double serviceFee;

  @Override
  public OrderCostDetails calculateOrderTotalCost(Order order, DeliveryMethod deliveryMethod,
      AddressCreateUpdateDTO address) {

    OrderCostDetails orderCostDetails = new OrderCostDetails();

    orderCostDetails.setOrderCost(MathUtils.getTwoDecimalPlaces(order.getTotal()));

    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(order.getTotal())).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.HALF_UP).doubleValue();

//    orderCostDetails.setStripeFee(stripeFee);

    // calculate this
    orderCostDetails.setDeliveryFee(0.0);

    // 6.10% utah tax

    double taxFee = BigDecimal.valueOf(6.10).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(order.getTotal())).doubleValue();

    orderCostDetails.setTaxFee(MathUtils.getTwoDecimalPlaces(taxFee));
    
    orderCostDetails.setServiceFee(MathUtils.getTwoDecimalPlaces(serviceFee + stripeFee));
    
    return orderCostDetails;
  }

}
