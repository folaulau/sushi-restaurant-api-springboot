package com.sushi.api.dataloader;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.account.AccountDAO;
import com.sushi.api.entity.role.Role;
import com.sushi.api.entity.role.UserType;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.entity.user.UserStatus;
import com.sushi.api.utils.PasswordUtils;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"local", "prod", "github"})
@Component
public class AccountDataLoader implements ApplicationRunner {

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private UserDAO    userDAO;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        int numberOfAccount = 10;

        for (int i = 1; i <= numberOfAccount; i++) {
            Account account = new Account();
            account.setId(Long.parseLong(i+""));
            account = accountDAO.save(account);

            User user = new User();
            user.setId(Long.parseLong(i+""));
            user.setAccount(account);

            String firstName = RandomGeneratorUtils.getRandomFirstname();

            user.setFirstName(firstName);

            String lastName = RandomGeneratorUtils.getRandomLastname();

            user.setLastName(lastName);

            if(i==3) {
                user.setEmail("folaudev+3@gmail.com");
            }else {
                user.setEmail((firstName + lastName).toLowerCase() + "@gmail.com");
            }
            
            
            user.setStatus(UserStatus.ACTIVE);

            user.setDob(LocalDate.now().minusYears(RandomGeneratorUtils.getIntegerWithin(10, 40)));

            user.setPhoneNumber("310" + RandomGeneratorUtils.getIntegerWithin(100, 999) + "" + RandomGeneratorUtils.getIntegerWithin(1000, 9999));
            
            user.addRole(Role.builder()
                    .userType(UserType.user)
                    .build());
            
            user.setPassword(PasswordUtils.hashPassword(PasswordUtils.DEV_PASSWORD));
            
            userDAO.save(user);
        }

    }

}
