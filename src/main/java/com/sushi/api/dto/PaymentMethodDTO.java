package com.sushi.api.dto;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(value = Include.NON_NULL)
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDTO {

  private Long id;

  /**
   * card, bank
   */
  private String type;

  private String uuid;

  private String name;

  private String last4;

  private String brand;

  private Long expirationMonth;

  private Long expirationYear;

  private boolean deleted;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
