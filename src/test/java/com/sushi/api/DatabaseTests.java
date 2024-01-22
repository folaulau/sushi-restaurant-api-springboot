package com.sushi.api;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.account.AccountDAO;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.utils.ObjectUtils;
import com.sushi.api.utils.PasswordUtils;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class DatabaseTests extends IntegrationTestConfiguration {

  @Autowired
  private AccountDAO accountDAO;

  @Autowired
  private UserDAO userDAO;

  @Transactional
  @Test
  void test_saveUser() {

    Account account = new Account();
    account = accountDAO.save(account);
    
    assertThat(account).isNotNull();
    assertThat(account.getId()).isNotNull().isGreaterThan(0);
    
    User user = new User();
    user.setAccount(account);

    String firstName = RandomGeneratorUtils.getRandomFirstname();

    user.setFirstName(firstName);

    String lastName = RandomGeneratorUtils.getRandomLastname();

    user.setLastName(lastName);

    user.setEmail((firstName + lastName).toLowerCase() + "@gmail.com");

    user.setDob(LocalDate.now().minusYears(RandomGeneratorUtils.getIntegerWithin(10, 40)));

    user.setPhoneNumber("310" + RandomGeneratorUtils.getIntegerWithin(100, 999) + ""
        + RandomGeneratorUtils.getIntegerWithin(1000, 9999));
    
    user.setPassword(PasswordUtils.hashPassword("Test1234!"));

    user = userDAO.save(user);

    log.info("user={}", ObjectUtils.toJson(user));
    
    assertThat(user).isNotNull();
    assertThat(user.getId()).isNotNull().isGreaterThan(0);
  }

}
