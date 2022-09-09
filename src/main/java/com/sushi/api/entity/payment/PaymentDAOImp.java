package com.sushi.api.entity.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PaymentDAOImp implements PaymentDAO {

  @Autowired
  private PaymentRepository paymentRepository;
}
