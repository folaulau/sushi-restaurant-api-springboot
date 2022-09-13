package com.sushi.api.entity.order;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum OrderStatus {

  ORDERING, ORDER_PLACED, PREPARING_ORDER, READY_FOR_PICK_UP, PICKED_UP, DELIVERING, DELIVERED;
  
  
  /**
   * PICK UP - ORDERING, ORDER_PLACED, PREPARING_ORDER, READY_FOR_PICK_UP, PICKED_UP<br>
   * DROP OFF - ORDERING, ORDER_PLACED, PREPARING_ORDER, DELIVERING, DELIVERED;
   */
}
