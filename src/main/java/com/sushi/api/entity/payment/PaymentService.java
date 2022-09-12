package com.sushi.api.entity.payment;

import com.stripe.model.PaymentIntent;
import com.sushi.api.entity.order.Order;
import com.sushi.api.entity.paymentmethod.PaymentMethod;

public interface PaymentService {

//	Payment payOrder(Order order, PaymentMethod paymentMethod);
//	
	Payment addOrderPayment(Order order, PaymentIntent paymentIntent);
}
