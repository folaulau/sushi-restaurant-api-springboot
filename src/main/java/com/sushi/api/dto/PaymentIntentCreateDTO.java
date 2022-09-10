package com.sushi.api.dto;

import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.order.DeliveryMethod;
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
public class PaymentIntentCreateDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String paymentIntentId;

  private boolean savePaymentMethod;

  private String userUuid;

  private DeliveryMethod deliveryMethod;

  private AddressCreateUpdateDTO address;

  @NotEmpty(message = "orderUuid is required")
  private String orderUuid;

}
