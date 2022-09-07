package com.sushi.api.library.stripe.paymentintent;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.PaymentIntentDTO;
import com.sushi.api.dto.PaymentIntentCreateDTO;
import com.sushi.api.utils.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Stripe PaymentIntents", description = "Stripe PaymentIntents")
@Slf4j
@RestController
public class StripePaymentIntentRestController {

  @Autowired
  private StripePaymentIntentService stripePaymentIntentService;

  @Operation(summary = "Get Stripe Payment Intent for order", description = "")
  @PostMapping(value = "/stripe/paymentintent/order")
  public ResponseEntity<PaymentIntentDTO> createOrderPaymentIntentForBooking(
      @RequestBody PaymentIntentCreateDTO paymentIntentParentDTO) {
    log.info("createParentPaymentIntent={}", ObjectUtils.toJson(paymentIntentParentDTO));

    PaymentIntentDTO paymentIntent =
        stripePaymentIntentService.createPaymentIntent(paymentIntentParentDTO);

    return new ResponseEntity<>(paymentIntent, OK);
  }
}
