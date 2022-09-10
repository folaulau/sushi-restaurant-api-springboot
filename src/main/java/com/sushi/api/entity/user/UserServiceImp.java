package com.sushi.api.entity.user;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.stripe.model.Customer;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.AuthenticatorDTO;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.account.AccountDAO;
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.role.Role;
import com.sushi.api.entity.role.UserType;
import com.sushi.api.exception.ApiException;
import com.sushi.api.library.firebase.FirebaseAuthService;
import com.sushi.api.library.stripe.customer.StripeCustomerService;
import com.sushi.api.security.AuthenticationService;
import com.sushi.api.utils.ObjectUtils;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImp implements UserService {

  @Autowired
  private FirebaseAuthService firebaseAuthService;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private StripeCustomerService stripeCustomerService;

  @Autowired
  private AccountDAO accountDAO;

  @Autowired
  private UserDAO userDAO;

  @Override
  public AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO) {
    UserRecord userRecord = firebaseAuthService.verifyAndGetUser(authenticatorDTO.getToken());

    log.info("userRecord: uuid={}, email={}", userRecord.getUid(), userRecord.getEmail());

    Optional<User> optUser = userDAO.findByUuid(userRecord.getUid());

    User user = null;

    if (optUser.isPresent()) {
      /**
       * sign in
       */
      user = optUser.get();

      if (!user.isActive()) {
        throw new ApiException("Your account is not active. Please contact our support team.",
            "status=" + user.getStatus());
      }

    } else {
      /**
       * sign up
       */
      
      Account account = new Account();
      account = accountDAO.save(account);

      user = new User();
      user.setUuid(userRecord.getUid());
      user.addRole(new Role(UserType.user));
      user.setAddress(new Address());
      user.setStatus(UserStatus.ACTIVE);
      user.setAccount(account);

      String email = userRecord.getEmail();

      if (email == null || email.isEmpty()) {
        UserInfo[] userInfos = userRecord.getProviderData();

        Optional<String> optEmail = Arrays.asList(userInfos).stream()
            .filter(userInfo -> (userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()))
            .map(userInfo -> userInfo.getEmail()).findFirst();

        if (optEmail.isPresent()) {
          email = optEmail.get();

          optUser = userDAO.findByEmail(email);
          if (optUser.isPresent()) {
            throw new ApiException("Email taken", "an account has this email already",
                "Please use one email per account");
          }
        } else {
          // temp email as placeholder
          email = "tempUser" + RandomGeneratorUtils.getIntegerWithin(10000, Integer.MAX_VALUE)
              + "@sushi.com";


          user.setEmailTemp(true);
        }
      }

      user.setThirdPartyName(userRecord.getDisplayName());

      user.setEmail(email);

      if (userRecord.getPhoneNumber() != null) {
        user.setPhoneNumber(userRecord.getPhoneNumber());
      }


      // com.stripe.model.Customer customer = stripeCustomerService.createParentDetails(petParent);
      //
      // user.setStripeCustomerId(customer.getId());

      user = userDAO.save(user);

      Customer stripeCustomer = stripeCustomerService.create(user);

      account.setStripeCustomerId(stripeCustomer.getId());

      accountDAO.save(account);

      // notificationService.sendWelcomeNotificationToParent(petParent);
    }

    AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(user);

    log.info("authenticationResponseDTO={}", ObjectUtils.toJson(authenticationResponseDTO));

    return authenticationResponseDTO;
  }

}
