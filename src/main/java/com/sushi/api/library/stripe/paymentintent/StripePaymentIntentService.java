package com.sushi.api.library.stripe.paymentintent;

import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.sushi.api.dto.PaymentIntentDTO;
import com.sushi.api.dto.PaymentIntentCreateDTO;

public interface StripePaymentIntentService {

  PaymentIntent getById(String paymentIntentId);

  PaymentIntentDTO createPaymentIntent(PaymentIntentCreateDTO paymentIntentParentDTO);

}
