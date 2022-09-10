package com.sushi.api.library.stripe.customer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.user.User;
import com.sushi.api.library.aws.secretsmanager.StripeSecrets;
import lombok.extern.slf4j.Slf4j;

/**
 * Parent Customer is created when paymentIntent is requested, refer to
 */
@Service
@Slf4j
public class StripeCustomerServiceImp implements StripeCustomerService {

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Value("${spring.profiles.active}")
  private String env;


  @Override
  public Customer getById(String id) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    Customer customer = null;

    try {
      customer = Customer.retrieve(id);
    } catch (Exception e) {
      log.warn("StripeException - createQuestPaymentIntent, customer, msg={}", e.getMessage());
    }

    return customer;
  }


  @Override
  public Customer create(User user) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    Customer customer = null;

    // @formatter:off
 
        CustomerCreateParams.Builder builder = CustomerCreateParams.builder();

        builder.setName(user.getFullName());
        builder.putMetadata("env",env);

        // @formatter:on

    if (user.getPhoneNumber() != null) {
      builder.setPhone(user.getPhoneNumber() + "");
    }

    Address address = user.getAddress();

    if (address != null) {
      builder.setAddress(CustomerCreateParams.Address.builder().setCity(address.getCity())
          .setCountry(address.getCountry()).setLine1(address.getStreet())
          .setPostalCode(address.getZipcode()).setState(address.getState()).build());
    }

    CustomerCreateParams createParams = builder.build();

    try {
      customer = Customer.create(createParams);

    } catch (StripeException e) {
      log.warn(
          "StripeException - createParentDetails, msg={}, userMessage={}, stripeErrorMessage={}",
          e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
    }

    return customer;
  }

  @Override
  public Customer addPaymentMethod(User user, PaymentMethod stripePaymentMethod) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    Customer customer = null;

    // @formatter:off
 
        CustomerUpdateParams.Builder builder = CustomerUpdateParams.builder()
                .setDefaultSource(stripePaymentMethod.getId());

        // @formatter:on

    CustomerUpdateParams updateParams = builder.build();

    try {
      customer = Customer.retrieve(user.getAccount().getStripeCustomerId());

      customer = customer.update(updateParams);

    } catch (StripeException e) {
      log.warn("StripeException - addPaymentMethod, msg={}, userMessage={}, stripeErrorMessage={}",
          e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
    }

    return customer;
  }

}
