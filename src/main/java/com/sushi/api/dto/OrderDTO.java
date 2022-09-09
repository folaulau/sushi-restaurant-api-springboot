package com.sushi.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

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
public class OrderDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  private Set<LineItemDTO> lineItems;

  private int totalItemCount;

  private double total;

  private boolean delivered;

  private LocalDateTime deliveredAt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private boolean paid;

  private LocalDateTime paidAt;

  public void setLineItems(Set<LineItemDTO> lineItems) {
    this.lineItems = lineItems;

    if (this.lineItems != null) {
      this.lineItems.forEach((lineItem) -> {
        this.totalItemCount += lineItem.getCount();
      });
    }
  }

  public int getTotalItemCount() {
    this.totalItemCount = 0;
    if (this.lineItems != null) {
      this.lineItems.forEach((lineItem) -> {
        this.totalItemCount += lineItem.getCount();
      });
    }
    return totalItemCount;
  }

}
