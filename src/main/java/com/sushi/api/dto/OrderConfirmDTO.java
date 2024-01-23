package com.sushi.api.dto;

import java.io.Serializable;
import java.util.Set;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Order has been paid for in frontend. Verify and update Order details
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class OrderConfirmDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull(message = "uuid is required")
  private String uuid;

  @NotNull(message = "paymentIntentId is required")
  private String paymentIntentId;

}
