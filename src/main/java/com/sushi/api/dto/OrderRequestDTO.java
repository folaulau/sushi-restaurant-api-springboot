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
 * Increase or Decrease LineItem count
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class OrderRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String uuid;

  private String userUuid;

  private LineItemCreateDTO lineItem;

}
