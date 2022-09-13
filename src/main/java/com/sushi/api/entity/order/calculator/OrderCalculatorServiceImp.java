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
import com.sushi.api.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderCalculatorServiceImp implements OrderCalculatorService {

  @Value("${spring.profiles.active}")
  private String env;

  /**
   * $2 per order
   */
  @Value("${service.fee:2}")
  private Double serviceFee;

  /**
   * $1.50 per mile
   */
  @Value("${service.fee:1.25}")
  private Double deliveryPerMileFee;

  @Override
  public OrderCostDetails calculateOrderTotalCost(Order order, DeliveryMethod deliveryMethod,
      AddressCreateUpdateDTO address) {

    OrderCostDetails orderCostDetails = new OrderCostDetails();

    double lineItemsTotal = MathUtils.getTwoDecimalPlaces(order.getLineItemsTotal());

    orderCostDetails.setLineItemsTotal(lineItemsTotal);

    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(lineItemsTotal)).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.HALF_UP).doubleValue();

    orderCostDetails.setStripeFee(stripeFee);

    /**
     * McDonald's at Traverse Mountain as my Sushi location<br>
     * 3550 Digital Dr, Lehi, UT 84043<br>
     * latitude = 40.432290<br>
     * longitude = -111.886480
     */

    double latitude = 40.432290;
    double longitude = -111.886480;

    double deliveryFee = 0.0;
    Double dropOffDistance = 0.0;


    if (deliveryMethod.equals(DeliveryMethod.DROP_OFF)) {
      dropOffDistance =
          MathUtils.distance(latitude, address.getLatitude(), longitude, address.getLongitude());
    }

    if (dropOffDistance != null) {
      deliveryFee = deliveryPerMileFee * dropOffDistance;
    }

    orderCostDetails.setDropOffDistance(dropOffDistance);

    // calculate this
    orderCostDetails.setDeliveryFee(deliveryFee);

    // 6.10% utah tax

    double taxFee = BigDecimal.valueOf(6.10).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(lineItemsTotal)).doubleValue();

    orderCostDetails.setTaxFee(MathUtils.getTwoDecimalPlaces(taxFee));

    orderCostDetails.setServiceFee(MathUtils.getTwoDecimalPlaces(serviceFee));
    
    log.info("orderCostDetails={}", ObjectUtils.toJson(orderCostDetails));

    return orderCostDetails;
  }

}
