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
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.order.DeliveryMethod;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderCostDetails;
import com.sushi.api.entity.order.OrderDAO;
import com.sushi.api.entity.order.OrderValidatorService;
import com.sushi.api.entity.order.calculator.OrderCalculatorService;
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

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private OrderCalculatorService orderCalculatorService;

  @Autowired
  private OrderDAO orderDAO;

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

    Triple<User, PaymentIntent, Order> triple =
        stripePaymentIntentValidatorService.validateCreatePaymentIntent(paymentIntentParentDTO);

    User user = triple.getLeft();

    PaymentIntent paymentIntent = triple.getMiddle();

    Order order = triple.getRight();

    OrderCostDetails orderCostDetails = orderCalculatorService.calculateOrderTotalCost(order,
        paymentIntentParentDTO.getDeliveryMethod(), paymentIntentParentDTO.getDeliveryAddress());

    long totalChargeAsCents = BigDecimal.valueOf(orderCostDetails.getTotal())
        .multiply(BigDecimal.valueOf(100)).longValue();

    if (paymentIntent != null) {

      try {

        // @formatter:off

        com.stripe.param.PaymentIntentUpdateParams.Builder builder = com.stripe.param.PaymentIntentUpdateParams.builder()
                .setAmount(totalChargeAsCents)
                .putMetadata("orderCostDetails", orderCostDetails.toJson())
                .putMetadata("env", env);

        builder.setCustomer(user.getAccount().getStripeCustomerId());

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
              .putMetadata("orderCostDetails", orderCostDetails.toJson())
              .putMetadata("env", env);
      //@formatter:on

      builder.setCustomer(user.getAccount().getStripeCustomerId());

      PaymentIntentCreateParams createParams = builder.build();

      try {
        paymentIntent = PaymentIntent.create(createParams);
        System.out.println("createParentPaymentIntent paymentIntent=" + paymentIntent.toJson());
      } catch (StripeException e) {
        log.warn("StripeException - createParentPaymentIntent, msg={}", e.getMessage());
        throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
      }

    }

    PaymentIntentDTO paymentIntentDTO =
        entityDTOMapper.mapOrderCostDetailsToPaymentIntent(orderCostDetails);

    paymentIntentDTO.setStripeCustomerId(user.getAccount().getStripeCustomerId());

    try {
      com.stripe.model.EphemeralKey key = com.stripe.model.EphemeralKey.create(
          EphemeralKeyCreateParams.builder().setCustomer(user.getAccount().getStripeCustomerId())
              .build(),
          RequestOptions.builder().setReadTimeout(60 * 1000)
              .setStripeVersionOverride(Stripe.API_VERSION).build());
      paymentIntentDTO.setEphemeralKey(key.getSecret());

    } catch (StripeException e) {
      log.warn("StripeException - createParentPaymentIntent EphemeralKey, msg={}", e.getMessage());
    }

    paymentIntentDTO.setId(paymentIntent.getId());
    paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
    paymentIntentDTO.setSetupFutureUsage(paymentIntent.getSetupFutureUsage());

    return paymentIntentDTO;
  }

  @Override
  public PaymentIntentDTO createGuestPaymentIntent(PaymentIntentCreateDTO paymentIntentParentDTO) {
    Triple<User, PaymentIntent, Order> triple = stripePaymentIntentValidatorService
        .validateGuestCreatePaymentIntent(paymentIntentParentDTO);

    PaymentIntent paymentIntent = triple.getMiddle();

    Order order = triple.getRight();

    OrderCostDetails orderCostDetails = orderCalculatorService.calculateOrderTotalCost(order,
        paymentIntentParentDTO.getDeliveryMethod(), paymentIntentParentDTO.getDeliveryAddress());

    long totalChargeAsCents = BigDecimal.valueOf(orderCostDetails.getTotal())
        .multiply(BigDecimal.valueOf(100)).longValue();

    if (paymentIntent != null) {

      try {

        // @formatter:off

        com.stripe.param.PaymentIntentUpdateParams.Builder builder = com.stripe.param.PaymentIntentUpdateParams.builder()
                .setAmount(totalChargeAsCents)
                .putMetadata("orderCostDetails", orderCostDetails.toJson())
                .putMetadata("env", env);


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
              .putMetadata("orderCostDetails", orderCostDetails.toJson())
              .putMetadata("env", env);
      //@formatter:on


      PaymentIntentCreateParams createParams = builder.build();

      try {
        paymentIntent = PaymentIntent.create(createParams);
        System.out.println("createParentPaymentIntent paymentIntent=" + paymentIntent.toJson());
      } catch (StripeException e) {
        log.warn("StripeException - createParentPaymentIntent, msg={}", e.getMessage());
        throw new ApiException(e.getMessage(), "StripeException, msg=" + e.getMessage());
      }

    }

    order.setDeliveryMethod(paymentIntentParentDTO.getDeliveryMethod());
    
    order = entityDTOMapper.patchOrderWithCostDetails(orderCostDetails,order);

    if (paymentIntentParentDTO.getDeliveryMethod().equals(DeliveryMethod.DROP_OFF)) {
      
      Address address = order.getAddress()!=null ? order.getAddress() : new Address();

      address = entityDTOMapper.patchAddressWithAddressCreateUpdateDTO(paymentIntentParentDTO.getDeliveryAddress(),
          address
          );
      
      order.setAddress(address);

    }

    order = orderDAO.save(order);

    PaymentIntentDTO paymentIntentDTO =
        entityDTOMapper.mapOrderCostDetailsToPaymentIntent(orderCostDetails);
    paymentIntentDTO.setId(paymentIntent.getId());
    paymentIntentDTO.setClientSecret(paymentIntent.getClientSecret());
    paymentIntentDTO.setSetupFutureUsage(paymentIntent.getSetupFutureUsage());

    return paymentIntentDTO;
  }
}
