package com.sushi.api.entity.order;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import org.apache.commons.lang3.tuple.Triple;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.EntityDTOMapper;
import com.sushi.api.dto.LineItemCreateDTO;
import com.sushi.api.dto.OrderConfirmDTO;
import com.sushi.api.dto.OrderDTO;
import com.sushi.api.dto.OrderRemoveRequestDTO;
import com.sushi.api.dto.OrderRequestDTO;
import com.sushi.api.entity.order.calculator.OrderCalculatorService;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.order.lineitem.LineItemDAO;
import com.sushi.api.entity.payment.Payment;
import com.sushi.api.entity.payment.PaymentService;
import com.sushi.api.entity.product.Product;
import com.sushi.api.entity.product.ProductDAO;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.user.User;
import com.sushi.api.exception.ApiException;
import com.sushi.api.utils.ObjectUtils;

@Service
public class OrderServiceImp implements OrderService {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private OrderDAO orderDAO;

  @Autowired
  private LineItemDAO lineItemDAO;

  @Autowired
  private OrderCalculatorService orderCalculatorService;

  @Autowired
  private ProductDAO productDAO;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private OrderValidatorService orderValidatorService;


  /**
   * Increase or Decrease LineItem count
   */
  @Override
  public OrderDTO createUpdateOrder(OrderRequestDTO orderRequestDTO) {
    Triple<Order, User, LineItemCreateDTO> triple =
        orderValidatorService.validateCreateUpdate(orderRequestDTO);

    Order order = triple.getLeft();

    User user = triple.getMiddle();

    LineItemCreateDTO lineItemCreateDTO = triple.getRight();

    String lineItemUuid = lineItemCreateDTO.getUuid();

    LineItem lineItem = null;

    if (lineItemUuid != null && !lineItemUuid.isEmpty()) {

      lineItem = order.getLineItem(lineItemCreateDTO.getProduct());

    } else {

      lineItem = entityDTOMapper.mapLineItemCreateDTOToLineItem(lineItemCreateDTO);

      Product product = productDAO.getByUuid(lineItemCreateDTO.getProduct().getUuid()).get();

      lineItem.setProduct(product);

    }

    lineItem.setCount(lineItemCreateDTO.getCount());
    order.addLineItem(lineItem);
    lineItem.setOrder(order);

    order.setUser(user);


    log.info("order={}", ObjectUtils.toJson(order));

    order = orderDAO.save(order);

    return entityDTOMapper.mapOrderToOrderDTO(order);
  }

  @Override
  public OrderDTO getByUuid(String uuid) {

    Optional<Order> optOrder = orderDAO.findByUuid(uuid);

    return entityDTOMapper.mapOrderToOrderDTO(optOrder.orElseThrow(
        () -> new ApiException("Order not found", "order not found for uuid=" + uuid)));
  }

  @Override
  public OrderDTO removeAll(String uuid) {
    Optional<Order> optOrder = orderDAO.findByUuid(uuid);

    Order order = optOrder
        .orElseThrow(() -> new ApiException("Order not found", "order not found for uuid=" + uuid));

    order.removeAllLineItems();

    order = orderDAO.save(order);

    return entityDTOMapper.mapOrderToOrderDTO(order);
  }

  @Override
  public OrderDTO remove(OrderRemoveRequestDTO orderRemoveRequestDTO) {

    log.info("remove={}", ObjectUtils.toJson(orderRemoveRequestDTO));

    Pair<Order, LineItem> pair = orderValidatorService.validateRemoval(orderRemoveRequestDTO);

    Order order = pair.getFirst();

    LineItem lineItem = pair.getSecond();

    if (orderRemoveRequestDTO.isAll()) {
      order.removeAllLineItems();
    } else {
      lineItem.setDeleted(true);
      lineItem.setCount(0);
      order.removeLineItem(lineItem);
    }

    order = orderDAO.save(order);

    return entityDTOMapper.mapOrderToOrderDTO(order);
  }

  @Override
  public OrderDTO confirmGuestPayment(OrderConfirmDTO orderConfirmDTO) {

    Pair<Order, PaymentIntent> pair = orderValidatorService.validatePayment(orderConfirmDTO);

    Order order = pair.getFirst();

    PaymentIntent paymentIntent = pair.getSecond();

    order.setPaid(paymentIntent.getStatus().equals("succeeded"));
    order.setPaidAt(LocalDateTime.now());

    Payment payment = paymentService.addOrderPayment(order, paymentIntent);

    order.setPayment(payment);
    order.setStatus(OrderStatus.ORDER_PLACED);

    order = orderDAO.save(order);

    return entityDTOMapper.mapOrderToOrderDTO(order);
  }

}
