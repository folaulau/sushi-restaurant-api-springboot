package com.sushi.api.entity.product;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ProductName {

  TUNA_ROLL(ProductType.SUSHI, "Tuna Roll", 12.0, "https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/rolls/tuna-roll.jpeg"), 
  SALMON_ROLL(ProductType.SUSHI, "Salmon Roll", 12.0 ,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/rolls/salmon-roll.jpeg"), 
  YAKI_ROLL(ProductType.SUSHI, "Yaki Roll",12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/rolls/yaki-niku-roll.jpeg"), 
  VEGETARIAN_ROLL(ProductType.SUSHI, "Vegetarian Roll", 12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/rolls/vegetarian-roll.jpeg"),


  FANTA(ProductType.DRINK, "Fanta", 12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/drinks/fanta.jpeg"), 
  COKE(ProductType.DRINK, "Coca Cola", 12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/drinks/coke.jpeg"), 
  ORANGE_JUICE(ProductType.DRINK, "Orange Juice",12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/drinks/orange.jpeg"), 
  HAWAIIAN_PUNCH(ProductType.DRINK, "Hawaiian Punch", 12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/drinks/punch.jpeg"),

  COOKIE(ProductType.DESSERT, "Cookies", 12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/desserts/cookies.jpeg"), 
  CHURRO(ProductType.DESSERT, "Churros",12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/desserts/churros.jpeg"), 
  BROWNIE(ProductType.DESSERT, "Brownies",12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/desserts/brownies.jpeg"), 
  ICE_CREAM(ProductType.DESSERT, "Ice Cream", 12.0,"https://s3.us-west-2.amazonaws.com/sushi.pocsoft.com/backend-resources/menu/desserts/ice-cream.jpeg");

  private String title;
  private Double price;
  private ProductType type;
  private String imgUrl;

  ProductName(ProductType type, String title, Double price, String imgUrl) {
    this.type = type;
    this.title = title;
    this.price = price;
    this.imgUrl = imgUrl;
  }
}
