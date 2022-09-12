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
    
    order = orderDAO.save(order);

    return entityDTOMapper.mapOrderToOrderDTO(order);
  }


  //
  // @Override
  // public Order create(Order order) {
  // log.debug("create(...)");
  // order.setId(new Long(0));
  // order.setUid(RandomGeneratorUtils.getOrderUid());
  // return orderRepository.saveAndFlush(order);
  // }
  //
  // @Override
  // public Order update(Order order) {
  // log.debug("update(...)");
  // if (order.getId() == null || order.getUid() == null) {
  // return create(order);
  // }
  //
  // if (order.getCustomer() != null) {
  // User user = ApiSessionUtils.getUser();
  // if (user != null && user.getId() != null) {
  // order.setCustomer(user);
  // }
  // }
  //
  // return orderRepository.saveAndFlush(order);
  // }
  //
  // @Override
  // public Order getByUid(String uid) {
  // log.debug("getByUid({})", uid);
  // return this.orderRepository.findByUid(uid);
  // }
  //
  // @Override
  // public Order getById(Long id) {
  // log.debug("getById({})", id);
  // return this.orderRepository.findById(id).orElse(null);
  // }
  //
  // @Override
  // public Order addLineItem(Order order, LineItem lineItem, boolean incrementing) {
  // log.debug("addLineItem(..)");
  // if (order.getLineItems().contains(lineItem)) {
  // log.debug("found match {}", ObjectUtils.toJson(lineItem));
  // LineItem existingLineItem = order.getLineItem(lineItem.getProduct());
  //
  // if (incrementing) {
  // existingLineItem.setCount(existingLineItem.getCount() + lineItem.getCount());
  // } else {
  // if (lineItem.getCount() <= 0) {
  // order.removeLineItem(existingLineItem);
  // } else {
  // existingLineItem.setCount(lineItem.getCount());
  // }
  // }
  // } else {
  // log.debug("not found match {}", ObjectUtils.toJson(lineItem));
  // lineItem.setOrder(order);
  // order.addLineItem(lineItem);
  // }
  //
  // return update(order);
  // }
  //
  // @Override
  // public Order addLineItem(String orderUid, LineItem lineItem, boolean incrementing) {
  // log.debug("addLineItem(..)");
  // Order order = null;
  // if (orderUid == null || orderUid.length() == 0) {
  // order = new Order();
  //
  // User customer = ApiSessionUtils.getUser();
  // if (customer != null && customer.getId() != null) {
  //
  // Order currentOrder = this.getCurrentByCustomerId(customer.getId());
  //
  // if (currentOrder != null) {
  // return addLineItem(currentOrder, lineItem, incrementing);
  // }
  //
  // order.setCustomer(customer);
  // }
  //
  // lineItem.setOrder(order);
  // order.addLineItem(lineItem);
  //
  // return create(order);
  // }
  //
  // order = this.getByUid(orderUid);
  //
  // if (order != null) {
  // return addLineItem(order, lineItem, incrementing);
  // }
  //
  // return order;
  // }
  //
  // @Override
  // public Order removeLineItem(Order order, LineItem lineItem) {
  //
  // order.getLineItems().remove(order.getLineItem(lineItem.getProduct()));
  //
  // return update(order);
  // }
  //
  // @Override
  // public Order payOrder(boolean useCardOnFile, Order order, PaymentMethod paymentMethod) {
  // log.debug("payOrder(..)");
  //// log.debug("order={}", ObjectUtils.toJson(order));
  //// log.debug("paymentMethod={}", ObjectUtils.toJson(paymentMethod));
  // Payment payment = null;
  //
  // User customer = order.getCustomer();
  //
  // if(useCardOnFile && customer!=null && paymentMethod.getId()>0) {
  //// log.debug("pay with existing payment method");
  // payment = paymentService.payOrder(customer.getPaymentGatewayId(), order, paymentMethod);
  // }else {
  // payment = paymentService.payOrder(order, paymentMethod);
  // }
  //
  // order.stampPayment(payment);
  // order.setCurrent(false);
  // return this.update(order);
  // }
  //
  // @Override
  // public Page<Order> getPage(Pageable pageable) {
  // // TODO Auto-generated method stub
  // return this.orderRepository.findAll(pageable);
  // }
  //
  // @Override
  // public Page<OrderAdminSearchResponseItemDTO> search(Pageable pageable, List<Integer> amounts,
  // String query,
  // List<Sorting> sortings) {
  // log.debug("search(...)");
  // StringBuilder queryBuilder = new StringBuilder();
  //
  // String selecteStatement = "SELECT cusOrder.id as orderId, cusOrder.uid as orderUid,
  // cusOrder.total, cusOrder.delivered, cusOrder.paid, cusOrder.paid_at as paidAt, "
  // + "cusOrder.updated_at as updatedAt, "
  // + "addr.city, addr.state, "
  // + "customer.uid as customerUid, CONCAT(customer.first_name, ' ', customer.last_name) as
  // customerName ";
  //
  // queryBuilder.append(" FROM customer_order as cusOrder ");
  //
  // queryBuilder.append(" LEFT JOIN user as customer ON cusOrder.customer_id = customer.id ");
  // queryBuilder.append(" LEFT JOIN address as addr ON cusOrder.location_address_id = addr.id ");
  //
  //
  // queryBuilder.append(" WHERE 1=1 ");
  //
  // if (query != null && query.isEmpty() == false) {
  //
  // queryBuilder.append(" AND((customer.first_name LIKE :q) OR ");
  // queryBuilder.append(" (customer.last_name LIKE :q) OR ");
  // queryBuilder.append(" (cusOrder.id LIKE :q) OR ");
  // queryBuilder.append(" (cusOrder.total LIKE :q) OR ");
  // queryBuilder.append(" (addr.city LIKE :q) OR ");
  // queryBuilder.append(" (addr.state LIKE :q)) ");
  // }
  //
  // int numberOfAmounts = (amounts != null) ? amounts.size() : 0;
  //
  // if (numberOfAmounts > 0) {
  // Collections.sort(amounts);
  // queryBuilder.append(" AND (");
  // for (int i = 0; i < numberOfAmounts; i++) {
  // MinMax minmax = AdminOrderSearchFilter.getFilterAmount(amounts.get(i));
  // if (i < (numberOfAmounts - 1)) {
  // queryBuilder.append(
  // " (cusOrder.total BETWEEN " + minmax.getMin() + " AND " + minmax.getMax() + ") OR ");
  // } else {
  // queryBuilder
  // .append(" (cusOrder.total BETWEEN " + minmax.getMin() + " AND " + minmax.getMax() + ")");
  // }
  //
  // }
  // queryBuilder.append(")");
  // }
  //
  // // ========== Sort ============
  // queryBuilder.append(" ORDER BY cusOrder.updated_at DESC ");
  //
  // String searchQuery = selecteStatement + queryBuilder.toString();
  //
  // log.debug("QUERY: {}", searchQuery);
  //
  // Query searchingQuery =
  // this.entityManager.createNativeQuery(searchQuery).unwrap(org.hibernate.query.Query.class)
  // .setResultTransformer(new AliasToBeanResultTransformer(OrderAdminSearchResponseItemDTO.class));
  //
  // if (query != null && query.isEmpty() == false) {
  // searchingQuery.setParameter("q", "%" + query + "%");
  // }
  //
  // searchingQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
  //
  // searchingQuery.setMaxResults(pageable.getPageSize());
  //
  // @SuppressWarnings("unchecked")
  // List<OrderAdminSearchResponseItemDTO> result = searchingQuery.getResultList();
  //
  // log.debug("result={}",ObjectUtils.toJson(result));
  //
  // String countQuery = "SELECT COUNT(cusOrder.id) " + queryBuilder.toString();
  //
  // log.debug("countQuery={}",ObjectUtils.toJson(countQuery));
  //
  // Query countingQuery = this.entityManager.createNativeQuery(countQuery);
  //
  // if (query != null && query.isEmpty() == false) {
  // countingQuery.setParameter("q", "%" + query + "%");
  // }
  //
  // long totalMatch = new Long(countingQuery.getSingleResult().toString());
  //
  // log.debug("totalMatch={}", totalMatch);
  //
  // PageImpl<OrderAdminSearchResponseItemDTO> page = new
  // PageImpl<OrderAdminSearchResponseItemDTO>(result,pageable,totalMatch);
  //
  // return page;
  // }
  //
  // @Override
  // public Page<Order> getPage(String customerUid, Pageable pageable) {
  // PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
  // Sort.by(Sort.Direction.DESC, "paid","delivered","updatedAt"));
  // return this.orderRepository.findByCustomerUid(customerUid, pageRequest);
  // }
  //
  //// @Override
  //// public String getLatestOrderUid(Long customerId) {
  //// // TODO Auto-generated method stub
  //// return this.orderRepository.getLatestOrderUidByCustomerId(customerId);
  //// }
  ////
  //// @Override
  //// public Order getLatestOrder(Long customerId) {
  //// // TODO Auto-generated method stub
  //// return orderRepository.getLatestOrderByCustomerId(customerId);
  //// }
  //
  // @Override
  // public Order getCurrentByCustomerId(Long customerId) {
  // // TODO Auto-generated method stub
  // return orderRepository.getCurrentOrderByCustomerId(customerId);
  // }
  //
  //


}
