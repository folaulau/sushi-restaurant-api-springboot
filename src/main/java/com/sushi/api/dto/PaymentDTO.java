package com.sushi.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.order.paymentmethod.OrderPaymentMethod;
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
public class PaymentDTO implements Serializable{

  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  private String type;

  private Long orderId;

  private Boolean paid;

  private String description;

  private PaymentMethodDTO paymentMethod;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
