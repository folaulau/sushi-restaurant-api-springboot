package com.sushi.api.dataloader;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.account.AccountDAO;
import com.sushi.api.entity.product.Product;
import com.sushi.api.entity.product.ProductDAO;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Profile(value = {"local", "prod", "github"})
@Component
public class ProductDataLoader implements ApplicationRunner {

  @Autowired
  private ProductDAO productDAO;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    List<ProductName> productNames = Arrays.asList(ProductName.values());

    for (int i = 0; i < productNames.size(); i++) {
      ProductName productName = productNames.get(i);

      long id = new Long(i + 1);

      Product product = new Product();
      product.setId(id);
      product.setUuid(productName);
      product.setPrice(productName.getPrice());
      product.setTitle(productName.getTitle());
      product.setType(productName.getType());
      product.setImageUrl(productName.getImgUrl());
      
      productDAO.save(product);
    }
  }

}
