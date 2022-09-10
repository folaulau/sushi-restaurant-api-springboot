package com.sushi.api.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sushi.api.utils.MathUtils;
import com.sushi.api.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class OrderCostDetails implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * cost of order(all products)
   */
  private Double orderCost;

  /**
   * Service fee, $2
   */
  private Double serviceFee;

  private Double deliveryFee;

  /**
   * stripe fee on charge<br>
   * 2.9% of orderCost<br>
   */
  private Double stripeFee;

  private Double taxFee;

  /**
   * total charge for everything = orderCost + serviceFee + stripeFee
   */
  private Double total;


  public String toJson() {
    return ObjectUtils.toJson(this);
  }

  /**
   * careServicesCost + pickUpCost + dropOffCost
   * 
   * @return
   */
  public Double getTotal() {
    BigDecimal orderTotal = BigDecimal.valueOf(0.0);

    orderTotal = orderTotal.add(BigDecimal.valueOf(orderCost));

    if (serviceFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(serviceFee));
    }

    if (deliveryFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(deliveryFee));
    }

    if (stripeFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(stripeFee));
    }

    if (taxFee != null) {
      orderTotal = orderTotal.add(BigDecimal.valueOf(taxFee));
    }

    total = orderTotal.doubleValue();

    return MathUtils.getTwoDecimalPlaces(total);
  }



  public static OrderCostDetails fromJson(String json) {
    if (json == null || json.trim().isEmpty()) {
      return null;
    }

    try {
      return ObjectUtils.getObjectMapper().readValue(json,
          new TypeReference<OrderCostDetails>() {});
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

}
