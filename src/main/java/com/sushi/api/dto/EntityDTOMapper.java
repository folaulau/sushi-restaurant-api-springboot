package com.sushi.api.dto;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.sushi.api.entity.address.Address;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.OrderCostDetails;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.order.paymentmethod.OrderPaymentMethod;
import com.sushi.api.entity.paymentmethod.PaymentMethod;
import com.sushi.api.entity.reservation.Reservation;
import com.sushi.api.entity.user.User;

// @formatter:off
@Mapper(componentModel = "spring", 
nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
unmappedTargetPolicy = ReportingPolicy.IGNORE,  
nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
//@formatter:on
public interface EntityDTOMapper {

  AuthenticationResponseDTO mapUserToAuthenticationResponse(User user);

  OrderPaymentMethod mapPaymentMethodToOrderPaymentMethod(PaymentMethod paymentMethod);

  OrderDTO mapOrderToOrderDTO(Order order);

  LineItem mapLineItemCreateDTOToLineItem(LineItemCreateDTO lineItemCreateDTO);

  PaymentIntentDTO mapOrderCostDetailsToPaymentIntent(OrderCostDetails orderCostDetails);

  @Mappings({@Mapping(target = "uuid", ignore = true)})
  Address patchAddressWithAddressCreateUpdateDTO(AddressCreateUpdateDTO deliveryAddress,
      @MappingTarget Address address);

  Order patchOrderWithCostDetails(OrderCostDetails orderCostDetails, @MappingTarget Order order);

  Reservation mapReservationCreateDTOToReservation(ReservationCreateDTO reservationCreateDTO);

  ReservationDTO mapReservationToReservationDTO(Reservation reservation);

  @Mappings({@Mapping(target = "uuid", ignore = true)})
  Reservation patchReservationWithReservationUpdateDTO(ReservationUpdateDTO reservationUpdateDTO,
      @MappingTarget Reservation reservation);

  UserDTO mapUserToUserDTO(User user);

  @Mappings({@Mapping(target = "uuid", ignore = true)})
  User patchUserWithUserUpdateDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

}
