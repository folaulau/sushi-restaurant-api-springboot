package com.sushi.api.entity.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stripe.model.Charge.PaymentMethodDetails;
import com.stripe.model.PaymentIntent;
import com.sushi.api.dto.EntityDTOMapper;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.order.paymentmethod.OrderPaymentMethod;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImp implements PaymentService {

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private PaymentDAO paymentDAO;

  /**
   * Payment has been made, now save a record of it
   */
  @Override
  public Payment addOrderPayment(Order order, PaymentIntent paymentIntent) {

    log.info("paymentIntent={}", paymentIntent.toJson());

    Payment payment = order.getPayment() != null ? order.getPayment() : new Payment();

    com.stripe.model.Charge charge = paymentIntent.getCharges().getData().get(0);

    PaymentMethodDetails paymentMethodDetails = charge.getPaymentMethodDetails();
    
    OrderPaymentMethod paymentMethod = new OrderPaymentMethod();
    paymentMethod.setStripePaymentMethodId(charge.getPaymentMethod());
    paymentMethod.setBrand(paymentMethodDetails.getCard().getBrand());
    paymentMethod.setLast4(paymentMethodDetails.getCard().getLast4());
    paymentMethod.setName(paymentMethodDetails.getCard().getDescription());

    payment.setOrderId(order.getId());
    payment.setStripeChargeId(charge.getId());
    payment.setPaymentMethod(paymentMethod);

    return paymentDAO.save(payment);
  }

}
