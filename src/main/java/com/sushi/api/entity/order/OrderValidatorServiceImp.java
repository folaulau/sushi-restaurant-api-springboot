package com.sushi.api.entity.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.LineItemCreateDTO;
import com.sushi.api.dto.LineItemDTO;
import com.sushi.api.dto.OrderConfirmDTO;
import com.sushi.api.dto.OrderRemoveRequestDTO;
import com.sushi.api.dto.OrderRequestDTO;
import com.sushi.api.dto.ProductUuidDTO;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.order.lineitem.LineItemDAO;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.exception.ApiException;
import com.sushi.api.library.stripe.paymentintent.StripePaymentIntentService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderValidatorServiceImp implements OrderValidatorService {

  @Autowired
  private OrderDAO orderDAO;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private LineItemDAO lineItemDAO;


  @Autowired
  private StripePaymentIntentService stripePaymentIntentService;

  @Override
  public Triple<Order, User, LineItemCreateDTO> validateCreateUpdate(
      OrderRequestDTO orderRequestDTO) {

    String userUuid = orderRequestDTO.getUserUuid();

    User user = userDAO.findByUuid(userUuid).orElse(null);

    String uuid = orderRequestDTO.getUuid();

    Order order = null;

    Optional<Order> optOrder = orderDAO.findByUuid(uuid);

    if (optOrder.isPresent()) {
      order = optOrder.get();

      if (user != null && order.getUser() != null && !order.getUser().equals(user)) {
        throw new ApiException("Wrong Order", "order does not belong to userUuid=" + userUuid);
      }

    } else {
      order = new Order();
    }

    LineItemCreateDTO lineItemCreateDTO = orderRequestDTO.getLineItem();

    String lineItemUuid = lineItemCreateDTO.getUuid();

    ProductUuidDTO productUuidDTO = lineItemCreateDTO.getProduct();

    if (lineItemUuid != null && !lineItemUuid.isEmpty()) {

      LineItem lineItem = order.getLineItem(productUuidDTO);

      if (lineItem == null) {
        throw new ApiException("Lineitem not found", "Lineitem not found for uuid=" + lineItemUuid);
      }

      if (!lineItem.getOrder().equals(order)) {
        throw new ApiException("Wrong Order", "lineItem does not belong to order=" + uuid);
      }

    } else {

      ProductUuidDTO product = lineItemCreateDTO.getProduct();

      if (product == null) {
        throw new ApiException("Product is empty");
      }

      ProductName productName = product.getUuid();

      if (productName == null) {
        throw new ApiException("Product name is empty");
      }

    }



    return Triple.of(order, user, lineItemCreateDTO);

  }


  @Override
  public Pair<Order, LineItem> validateRemoval(OrderRemoveRequestDTO orderRemoveRequestDTO) {

    String userUuid = orderRemoveRequestDTO.getUserUuid();

    User user = userDAO.findByUuid(userUuid).orElse(null);

    String uuid = orderRemoveRequestDTO.getUuid();

    Order order = orderDAO.findByUuid(uuid)
        .orElseThrow(() -> new ApiException("Order not found", "order not found for uuid=" + uuid));

    if (user != null && order.getUser() != null && !order.getUser().equals(user)) {
      throw new ApiException("Wrong Order", "order does not belong to userUuid=" + userUuid);
    }

    order.setUser(user);

    LineItem lineItem = null;

    if (!orderRemoveRequestDTO.isAll()) {
      LineItemCreateDTO lineItemCreateDTO = orderRemoveRequestDTO.getLineItem();

      String lineItemUuid = lineItemCreateDTO.getUuid();

      ProductUuidDTO productUuidDTO = lineItemCreateDTO.getProduct();

      lineItem = order.getLineItem(productUuidDTO);

      if (lineItem == null) {
        throw new ApiException("Lineitem not found", "Lineitem not found for uuid=" + lineItemUuid);
      }

      if (!lineItem.getOrder().equals(order)) {
        throw new ApiException("Wrong Order", "lineItem does not belong to order=" + uuid);
      }
    } else {
      lineItem = new LineItem();
    }

    return Pair.of(order, lineItem);
  }


  @Override
  public Pair<Order, PaymentIntent> validatePayment(OrderConfirmDTO orderConfirmDTO) {


    Order order = orderDAO.findByUuid(orderConfirmDTO.getUuid())
        .orElseThrow(() -> new ApiException("Order not found",
            "order not found for uuid=" + orderConfirmDTO.getUuid()));

    PaymentIntent paymentIntent =
        stripePaymentIntentService.getById(orderConfirmDTO.getPaymentIntentId());

    if (!paymentIntent.getStatus().equalsIgnoreCase("succeeded")) {
      if (paymentIntent.getStatus().equalsIgnoreCase("requires_payment_method")) {
        throw new ApiException("You need to make a payment", "order has not been paid for");
      }
    }

    return Pair.of(order, paymentIntent);
  }



}
