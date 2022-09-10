package com.sushi.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

  private List<LineItemDTO> lineItems;

  private int totalItemCount;

  private double total;

  private boolean delivered;

  private LocalDateTime deliveredAt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private boolean paid;

  private LocalDateTime paidAt;

  public void setLineItems(List<LineItemDTO> lineItems) {
    this.lineItems = lineItems;
    
    Collections.sort(this.lineItems, (l1, l2) -> {
      return (int) (l1.getId() - l2.getId());
    });

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
