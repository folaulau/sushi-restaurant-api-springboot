package com.sushi.api.library.stripe.paymentintent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.LineItemDTO;
import com.sushi.api.dto.OrderRequestDTO;
import com.sushi.api.dto.PaymentIntentCreateDTO;
import com.sushi.api.dto.ProductUuidDTO;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderDAO;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.product.Product;
import com.sushi.api.entity.product.ProductDAO;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.exception.ApiError;
import com.sushi.api.exception.ApiException;
import com.sushi.api.library.aws.secretsmanager.StripeSecrets;
import com.sushi.api.utils.ApiSessionUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripePaymentIntentValidatorServiceImp implements StripePaymentIntentValidatorService {

  @Autowired
  private ProductDAO productDAO;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private OrderDAO orderDAO;

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Override
  public Triple<User, PaymentIntent, Order> validateCreatePaymentIntent(
      PaymentIntentCreateDTO paymentIntentCreateDTO) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    String orderUuid = paymentIntentCreateDTO.getOrderUuid();

    Order order = orderDAO.findByUuid(orderUuid).orElseThrow(
        () -> new ApiException("Order not found", "order not found for uuid=" + orderUuid));

    PaymentIntent paymentIntent = null;

    String paymentIntentId = paymentIntentCreateDTO.getPaymentIntentId();

    if (paymentIntentId != null && !paymentIntentId.isEmpty()) {
      try {
        paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        // log.info("paymentIntent={}", paymentIntent.toJson());
      } catch (StripeException e) {
        log.warn("StripeException - getById, msg={}, userMessage={}, stripeErrorMessage={}",
            e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
        throw new ApiException(e.getStripeError().getMessage(),
            "Stripe error: " + e.getLocalizedMessage());

      }
    }

    String userUuid = paymentIntentCreateDTO.getUserUuid();

    User user = null;

    if (userUuid != null && !userUuid.isEmpty()) {

      user = userDAO.findByUuid(paymentIntentCreateDTO.getUserUuid()).orElseThrow(
          () -> new ApiException("User not found", "user not found for uuid=" + userUuid));

      Long sessionUserId = ApiSessionUtils.getUserId();

      if (!user.getId().equals(sessionUserId)) {
        throw new ApiException("User not found", "user not matched to login user");
      }

      if (!user.getId().equals(sessionUserId)) {
        throw new ApiException("User not found", "user not matched to login user");
      }
    }


    return Triple.of(user, paymentIntent, order);
  }

  @Override
  public Triple<User, PaymentIntent, Order> validateGuestCreatePaymentIntent(
      PaymentIntentCreateDTO paymentIntentCreateDTO) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    String orderUuid = paymentIntentCreateDTO.getOrderUuid();

    Order order = orderDAO.findByUuid(orderUuid).orElseThrow(
        () -> new ApiException("Order not found", "order not found for uuid=" + orderUuid));


    if (order.getUser() != null) {
      throw new ApiException(ApiError.DEFAULT_MSG,
          "unexpected user attached to order but it should be null");
    }

    PaymentIntent paymentIntent = null;

    String paymentIntentId = paymentIntentCreateDTO.getPaymentIntentId();

    if (paymentIntentId != null && !paymentIntentId.isEmpty()) {
      try {
        paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        // log.info("paymentIntent={}", paymentIntent.toJson());
      } catch (StripeException e) {
        log.warn("StripeException - getById, msg={}, userMessage={}, stripeErrorMessage={}",
            e.getLocalizedMessage(), e.getUserMessage(), e.getStripeError().getMessage());
        throw new ApiException(e.getStripeError().getMessage(),
            "Stripe error: " + e.getLocalizedMessage());

      }
    }
    
    User user = null;

    return Triple.of(user, paymentIntent, order);
  }

}
