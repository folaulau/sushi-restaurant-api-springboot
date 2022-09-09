package com.sushi.api.entity.order.paymentmethod;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@Embeddable
public class OrderPaymentMethod implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "payment_method_id")
  private Long id;

  @Column(name = "stripe_payment_method_id")
  private String stripePaymentMethodId;

  @Column(name = "source_token")
  private String sourceToken;

  @Column(name = "payment_method_type")
  private String type;

  @Column(name = "payment_method_name")
  private String name;

  @Column(name = "payment_method_last4")
  private String last4;

  @Column(name = "payment_method_brand")
  private String brand;

}
