package com.sushi.api.dataloader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import com.amazonaws.services.ecs.model.UpdateServiceResult;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.account.AccountDAO;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Profile(value = {"local"})
@Component
public class AccountDataLoader implements ApplicationRunner {

  @Autowired
  private AccountDAO accountDAO;

  @Autowired
  private UserDAO userDAO;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    int numberOfAccount = 10;

    for (int i = 1; i <= numberOfAccount; i++) {
      Account account = new Account();
      account.setId(new Long(i));
      account.setUuid("acct-uuid-" + i);
      account = accountDAO.save(account);

      User user = new User();
      user.setId(new Long(i));
      user.setUuid("user-uuid-" + i);
      user.setAccount(account);

      String firstName = RandomGeneratorUtils.getRandomFirstname();

      user.setFirstName(firstName);

      String lastName = RandomGeneratorUtils.getRandomLastname();

      user.setLastName(lastName);

      user.setEmail((firstName + lastName).toLowerCase() + "@gmail.com");

      user.setDob(LocalDate.now().minusYears(RandomGeneratorUtils.getIntegerWithin(10, 40)));

      user.setPhoneNumber("310" + RandomGeneratorUtils.getIntegerWithin(100, 999) + ""
          + RandomGeneratorUtils.getIntegerWithin(1000, 9999));

      userDAO.save(user);
    }


  }

}
