package com.sushi.api.dto;

import java.io.Serializable;
import java.util.List;
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
public class PaymentIntentCreateDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String paymentIntentId;

  private String userUuid;

  private List<ProductUuidDTO> products;

}
