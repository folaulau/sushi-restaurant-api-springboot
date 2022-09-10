package com.sushi.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class PaymentIntentDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String clientSecret;

  private String id;

  /**
   * stripe fee on charge
   */
  private Double stripeFee;

  /**
   * service fee on charge
   */
  private Double serviceFee;

  /**
   * amount charge for order
   */
  private Double orderCost;

  private Double deliveryFee;

  private Double taxFee;

  /**
   * amount charge for order + stripeFee + serviceFee + deliveryFee
   */
  private Double total;

  // for paymentMethod to use in future
  private String setupFutureUsage;

  /**
   * customer to see all paymentMethods
   */
  private String ephemeralKey;

  private String stripeCustomerId;

}
