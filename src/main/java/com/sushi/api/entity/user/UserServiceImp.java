package com.sushi.api.entity.user;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stripe.model.Customer;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.AuthenticatorDTO;
import com.sushi.api.dto.EntityDTOMapper;
import com.sushi.api.dto.UserDTO;
import com.sushi.api.dto.UserSignInDTO;
import com.sushi.api.dto.UserSignUpDTO;
import com.sushi.api.dto.UserUpdateDTO;
import com.sushi.api.entity.account.Account;
import com.sushi.api.entity.account.AccountDAO;
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.role.Role;
import com.sushi.api.entity.role.UserType;
import com.sushi.api.exception.ApiException;
import com.sushi.api.library.stripe.customer.StripeCustomerService;
import com.sushi.api.security.AuthenticationService;
import com.sushi.api.utils.ObjectUtils;
import com.sushi.api.utils.PasswordUtils;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImp implements UserService {

    // @Autowired
    // private FirebaseAuthService firebaseAuthService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private StripeCustomerService stripeCustomerService;

    @Autowired
    private AccountDAO            accountDAO;

    @Autowired
    private UserDAO               userDAO;

    @Autowired
    private EntityDTOMapper       entityDTOMapper;

    @Autowired
    private UserValidatorService  userValidatorService;

    @Override
    public User getByUuid(String uuid) throws ApiException {
        return userDAO.findByUuid(uuid).orElseThrow(() -> new ApiException("User not found"));
    }

    @Override
    public AuthenticationResponseDTO signUp(UserSignUpDTO userSignUpDTO) {
        log.info("signUp...");
        /**
         * sign up
         */

        User user = userValidatorService.validateSignUp(userSignUpDTO);

        // @formatter:off

        user = User.builder()
                .email(userSignUpDTO.getEmail())
                .firstName(userSignUpDTO.getFirstName())
                .lastName(userSignUpDTO.getLastName())
                .password(PasswordUtils.hashPassword(userSignUpDTO.getPassword()))
                .phoneNumber(userSignUpDTO.getPhoneNumber())
                .build();

        // @formatter:on
        
        Account account = new Account();
        account = accountDAO.save(account);

        user.addRole(new Role(UserType.user));

        Address address = new Address();
        user.setAddress(address);
        address.setUser(user);

        user.setStatus(UserStatus.ACTIVE);
        user.setAccount(account);

        user = userDAO.save(user);

        Customer stripeCustomer = stripeCustomerService.create(user);

        account.setStripeCustomerId(stripeCustomer.getId());

        account = accountDAO.save(account);

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(user);

        log.info("authenticationResponseDTO={}", ObjectUtils.toJson(authenticationResponseDTO));

        return authenticationResponseDTO;
    }

    @Override
    public AuthenticationResponseDTO signIn(UserSignInDTO userSignInDTO) {
        log.info("signIn...");

        User user = userValidatorService.validateSignIn(userSignInDTO);

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(user);

        log.info("authenticationResponseDTO={}", ObjectUtils.toJson(authenticationResponseDTO));

        return authenticationResponseDTO;
    }

    @Override
    public UserDTO getProfile(String uuid) {
        User user = getByUuid(uuid);
        return entityDTOMapper.mapUserToUserDTO(user);
    }

    @Override
    public UserDTO updateProfle(UserUpdateDTO userUpdateDTO) {

        User user = getByUuid(userUpdateDTO.getUuid());

        user = entityDTOMapper.patchUserWithUserUpdateDTO(userUpdateDTO, user);

        user = userDAO.save(user);

        return entityDTOMapper.mapUserToUserDTO(user);
    }
}
