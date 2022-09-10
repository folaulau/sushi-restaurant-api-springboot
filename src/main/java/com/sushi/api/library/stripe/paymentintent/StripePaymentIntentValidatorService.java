package com.sushi.api.library.stripe.paymentintent;

import java.util.List;
import org.apache.commons.lang3.tuple.Triple;
import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.OrderRequestDTO;
import com.sushi.api.dto.PaymentIntentCreateDTO;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.lineitem.LineItem;
import com.sushi.api.entity.user.User;

public interface StripePaymentIntentValidatorService {

  Triple<User, PaymentIntent, Order> validateGuestCreatePaymentIntent(PaymentIntentCreateDTO paymentIntentCreateDTO);
  
  Triple<User, PaymentIntent, Order> validateCreatePaymentIntent(PaymentIntentCreateDTO paymentIntentCreateDTO);

}
