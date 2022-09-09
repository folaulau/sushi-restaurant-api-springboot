package com.sushi.api.library.stripe.paymentintent;

import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.sushi.api.dto.EntityDTOMapper;
import com.sushi.api.dto.PaymentIntentDTO;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.product.Product;
import com.sushi.api.entity.user.User;
import com.sushi.api.dto.PaymentIntentCreateDTO;
import com.sushi.api.exception.ApiException;
import com.sushi.api.library.aws.secretsmanager.StripeSecrets;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripePaymentIntentServiceImp implements StripePaymentIntentService {

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Autowired
  private StripePaymentIntentValidatorService stripePaymentIntentValidatorService;

  @Value("${spring.profiles.active}")
  private String env;

  @Value("${booking.fee:10}")
  private Double bookingFee;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Override
  public PaymentIntent getById(String paymentIntentId) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    PaymentIntent paymentIntent = null;

    try {
      paymentIntent = PaymentIntent.retrieve(paymentIntentId);
      // log.info("paymentIntent={}", paymentIntent.toJson());
    } catch (StripeException e) {
      log.warn("StripeException - getById, msg={}, userMessage={}, stripeErrorMessage={}",
          e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
    }

    return paymentIntent;
  }

  @Override
  public PaymentIntentDTO createPaymentIntent(PaymentIntentCreateDTO paymentIntentParentDTO) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    Triple<User, PaymentIntent, List<LineItem>> triple =
        stripePaymentIntentValidatorService.validateCreatePaymentIntent(paymentIntentParentDTO);

    User user = triple.getLeft();

    PaymentIntent paymentIntent = triple.getMiddle();

    List<LineItem> products = triple.getRight();

    long totalChargeAsCents = BigDecimal.valueOf(123).multiply(BigDecimal.valueOf(100)).longValue();

    if (paymentIntent != null) {

      try {

        paymentIntent = PaymentIntent.retrieve(paymentIntentParentDTO.getPaymentIntentId());
        System.out.println(paymentIntent.toJson());

        // @formatter:off

        com.stripe.param.PaymentIntentUpdateParams.Builder builder = com.stripe.param.PaymentIntentUpdateParams.builder()
                .setAmount(totalChargeAsCents)
                .putMetadata("env", env);

        if (user!=null && user.getAccount().getStripeCustomerId() != null) {
          builder.setCustomer(user.getAccount().getStripeCustomerId());
        }

        com.stripe.param.PaymentIntentUpdateParams updateParams = builder.build();
        
        // @formatter:on

        paymentIntent = paymentIntent.update(updateParams);

        System.out.println("updateParentPaymentIntent paymentIntent=" + paymentIntent.toJson());
      } catch (StripeException e) {
        log.warn("StripeException - updateParentPaymentIntent, msg={}", e.getMessage());
        throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
      }


    } else {
      //@formatter:off
      com.stripe.param.PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
              .addPaymentMethodType("card")
              .setAmount(totalChargeAsCents)
              .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
              .setCurrency("usd")
              .putMetadata("env", env);
      //@formatter:on

      if (user != null && user.getAccount().getStripeCustomerId() != null) {
        builder.setCustomer(user.getAccount().getStripeCustomerId());
      }

      PaymentIntentCreateParams createParams = builder.build();

      try {
        paymentIntent = PaymentIntent.create(createParams);
        System.out.println("createParentPaymentIntent paymentIntent=" + paymentIntent.toJson());
      } catch (StripeException e) {
        log.warn("StripeException - createParentPaymentIntent, msg={}", e.getMessage());
        throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
      }

    }



    PaymentIntentDTO paymentIntentDTO = new PaymentIntentDTO();

    if (user != null && user.getAccount().getStripeCustomerId() != null) {
      
      paymentIntentDTO.setStripeCustomerId(user.getAccount().getStripeCustomerId());    
      
      try {
        com.stripe.model.EphemeralKey key = com.stripe.model.EphemeralKey.create(
            EphemeralKeyCreateParams.builder().setCustomer(user.getAccount().getStripeCustomerId())
                .build(),
            RequestOptions.builder().setReadTimeout(60 * 1000)
                .setStripeVersionOverride(Stripe.API_VERSION).build());
        paymentIntentDTO.setEphemeralKey(key.getSecret());

      } catch (StripeException e) {
        log.warn("StripeException - createParentPaymentIntent EphemeralKey, msg={}",
            e.getMessage());
      }
      
    }


    paymentIntentDTO.setId(paymentIntent.getId());
    paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
    paymentIntentDTO.setSetupFutureUsage(paymentIntent.getSetupFutureUsage());

    return paymentIntentDTO;
  }
}
