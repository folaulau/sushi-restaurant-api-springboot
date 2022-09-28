package com.sushi.api.entity.order;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sushi.api.dto.OrderConfirmDTO;
import com.sushi.api.dto.OrderDTO;
import com.sushi.api.dto.OrderRemoveRequestDTO;
import com.sushi.api.dto.OrderRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Orders", description = "Order Operations")
@RestController
@RequestMapping("/orders")
public class OrderController {

  @Autowired
  private OrderService orderService;


  @Operation(summary = "Create Order", description = "create or update order")
  @PostMapping("/current")
  public ResponseEntity<OrderDTO> createUpdateOrder(
      @RequestHeader(name = "token", required = false) String token, @Parameter(name = "Order",
          required = true, example = "order") @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
    log.debug("createUpdateOrder(token={})", token);

    OrderDTO orderDTO = orderService.createUpdateOrder(orderRequestDTO);

    return new ResponseEntity<>(orderDTO, HttpStatus.OK);
  }

  @Operation(summary = "Remove Item from Order", description = "remove item from order")
  @PutMapping("/current/remove-item")
  public ResponseEntity<OrderDTO> removeGuest(
      @RequestHeader(name = "token", required = false) String token,
      @Parameter(name = "OrderRemoval", required = true,
      example = "order removal") @Valid @RequestBody OrderRemoveRequestDTO orderRemoveRequestDTO) {
    log.debug("remove({})", orderRemoveRequestDTO.toString());

    OrderDTO orderDTO = orderService.remove(orderRemoveRequestDTO);

    return new ResponseEntity<>(orderDTO, HttpStatus.OK);
  }

  /**
   * Payment has been made in the frontend, verify and update order details
   */
  @Operation(summary = "Confirm Payment", description = "Confirm payment")
  @PutMapping("/confirm-payment")
  public ResponseEntity<OrderDTO> confirmQuestPayment(
      @RequestHeader(name = "token", required = false) String token,
      @Parameter(name = "OrderConfirm", required = true,
          example = "order confirmatioin") @Valid @RequestBody OrderConfirmDTO orderConfirmDTO) {
    log.debug("confirmQuestPayment({})", orderConfirmDTO.toString());

    OrderDTO orderDTO = orderService.confirmGuestPayment(orderConfirmDTO);

    return new ResponseEntity<>(orderDTO, HttpStatus.OK);
  }

  @Operation(summary = "Get Order", description = "get order")
  @GetMapping("/current")
  public ResponseEntity<OrderDTO> getOrder(
      @RequestHeader(name = "token", required = false) String token,
      @Parameter(name = "uuid", required = true, example = "uuid") @RequestParam String uuid) {
    log.debug("getOrder({})", uuid);

    OrderDTO orderDTO = orderService.getByUuid(uuid);

    return new ResponseEntity<>(orderDTO, HttpStatus.OK);
  }

}
