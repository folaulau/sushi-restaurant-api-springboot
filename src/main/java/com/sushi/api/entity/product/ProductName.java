package com.sushi.api.entity.product;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ProductName {

  TUNA_ROLL(ProductType.SUSHI, "Tuna Roll", 12.0), SALMON_ROLL(ProductType.SUSHI, "Salmon Roll",
      12.0), YAKI_ROLL(ProductType.SUSHI, "Yaki Roll",
          12.0), VEGETARIAN_ROLL(ProductType.SUSHI, "Vegetarian Roll", 12.0),


  FANTA(ProductType.DRINK, "Fanta", 12.0), COKE(ProductType.DRINK, "Coca Cola", 12.0), ORANGE_JUICE(
      ProductType.DRINK, "Orange Juice",
      12.0), HAWAIIAN_PUNCH(ProductType.DRINK, "Hawaiian Punch", 12.0),

  COOKIE(ProductType.DESSERT, "Cookies", 12.0), CHURRO(ProductType.DESSERT, "Churros",
      12.0), BROWNIE(ProductType.DESSERT, "Brownies",
          12.0), ICE_CREAM(ProductType.DESSERT, "Ice Cream", 12.0);

  private String title;
  private Double price;
  private ProductType type;

  ProductName(ProductType type, String title, Double price) {
    this.type = type;
    this.title = title;
    this.price = price;
  }
}
