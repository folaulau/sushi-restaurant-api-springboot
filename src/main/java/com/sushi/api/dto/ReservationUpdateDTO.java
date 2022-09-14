package com.sushi.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.utils.ObjectUtils;
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
public class ReservationUpdateDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String userUuid;

  @NotNull(message = "uuid is required")
  private String uuid;

  @NotNull(message = "name is required")
  private String name;

  @NotNull(message = "dateTime is required")
  private LocalDateTime dateTime;

  @Positive(message = "numberOfPeople must be greater than 0")
  private Integer numberOfPeople;

  public String toJson() {
    // TODO Auto-generated method stub
    return ObjectUtils.toJson(this);
  }

}
