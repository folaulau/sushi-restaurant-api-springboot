package com.sushi.api.dataloader;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.account.AccountDAO;
import com.sushi.api.entity.product.ProductDAO;
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
  public void run(ApplicationArguments args) throws Exception {}

}
