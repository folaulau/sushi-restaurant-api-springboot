package com.sushi.api.library.stripe.paymentintent;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.util.Pair;
import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.PaymentIntentCreateDTO;
import com.sushi.api.entity.product.Product;
import com.sushi.api.entity.user.User;

public interface StripePaymentIntentValidatorService {

  Triple<User, PaymentIntent, List<Product>> validateCreatePaymentIntent(
      PaymentIntentCreateDTO paymentIntentCreateDTO);

}
