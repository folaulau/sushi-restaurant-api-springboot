package com.sushi.api.entity.order;

import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sushi.api.dto.OrderConfirmDTO;
import com.sushi.api.dto.OrderDTO;
import com.sushi.api.dto.OrderRemoveRequestDTO;
import com.sushi.api.dto.OrderRequestDTO;

public interface OrderService {

  OrderDTO createUpdateOrder(@Valid OrderRequestDTO orderRequestDTO);

  OrderDTO getByUuid(String uuid);

  OrderDTO removeAll(String uuid);

  OrderDTO remove(OrderRemoveRequestDTO orderRemoveRequestDTO);

  OrderDTO confirmGuestPayment(OrderConfirmDTO orderConfirmDTO);

//	Order create(Order order);
//	
//	Order update(Order order);
//	
//	Order getByUid(String uid);
//	
////	String getLatestOrderUid(Long customerId);
////	
////	Order getLatestOrder(Long customerId);
//	
//	Order getCurrentByCustomerId(Long customerId);
//	
//	Order getById(Long id);
	
//	Order addLineItem(Order order, LineItem lineItem, boolean incrementing);
//	
//	Order removeLineItem(Order order, LineItem lineItem);
//	
//	Order addLineItem(String orderUid, LineItem lineItem, boolean incrementing);
//	
//	Order payOrder(boolean useCardOnFile, Order order, PaymentMethod paymentMethod);
//
//	Page<Order> getPage(Pageable pageable);
//	
//	Page<Order> getPage(String customerUid, Pageable pageable);
//
//	Page<OrderAdminSearchResponseItemDTO> search(Pageable pageable, List<Integer> amounts, String query, List<Sorting> sortings);
	
}
