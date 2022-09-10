package com.sushi.api.library.stripe.customer;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.sushi.api.entity.user.User;

public interface StripeCustomerService {

    com.stripe.model.Customer create(User user);

    Customer getById(String id);

    Customer addPaymentMethod(User user, PaymentMethod stripePaymentMethod);
}
