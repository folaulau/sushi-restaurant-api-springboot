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
import com.sushi.api.dto.LineItemCreateDTO;
import com.sushi.api.dto.LineItemDTO;
import com.sushi.api.dto.OrderRequestDTO;
import com.sushi.api.dto.ProductUuidDTO;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.order.lineitem.LineItemDAO;
import com.sushi.api.entity.product.ProductName;
import com.sushi.api.entity.user.User;
import com.sushi.api.entity.user.UserDAO;
import com.sushi.api.exception.ApiException;
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
  

  @Override
  public Triple<Order, User, Map<String, LineItem>> validateCreateUpdate(
      OrderRequestDTO orderRequestDTO) {


    String userUuid = orderRequestDTO.getUserUuid();

    User user = userDAO.findByUuid(userUuid).orElse(null);

    String uuid = orderRequestDTO.getUuid();

    Order order = null;

    Optional<Order> optOrder = orderDAO.findByUuid(uuid);

    if (optOrder.isPresent()) {
      order = optOrder.get();

      if (!order.getUser().equals(user)) {
        throw new ApiException("Wrong Order", "order does not belong to userUuid=" + userUuid);
      }

    } else {
      order = new Order();
    }

    Set<LineItemCreateDTO> lineItemCreateDTOs = orderRequestDTO.getLineItems();

    Map<String, LineItem> lineItems = new HashMap<>();

    if (lineItemCreateDTOs.isEmpty()) {
      throw new ApiException("Order is empty",
          "lineItems list is empty but must have at least 1 item");
    }

    for (LineItemCreateDTO lineItemCreateDTO : lineItemCreateDTOs) {

      String lineItemUuid = lineItemCreateDTO.getUuid();

      if (lineItemUuid != null && !lineItemUuid.isEmpty()) {

        LineItem lineItem = lineItemDAO.getByUuid(lineItemUuid)
            .orElseThrow(() -> new ApiException("Lineitem not found",
                "Lineitem not found for uuid=" + lineItemUuid));

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


    }



    return Triple.of(order, user, lineItems);

  }



}
