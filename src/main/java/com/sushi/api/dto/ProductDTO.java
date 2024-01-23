package com.sushi.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.product.ProductType;
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
public class ProductDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private ProductName uuid;

  private Integer rating;

  private Double price;

  private String imageUrl;

  private String title;

  private String description;

  private String calories;

  private ProductType type;


}
