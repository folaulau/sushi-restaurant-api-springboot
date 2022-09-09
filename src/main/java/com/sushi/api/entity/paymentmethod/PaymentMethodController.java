package com.sushi.api.entity.paymentmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fkaveinga
 *
 */
@Slf4j
@RestController
@RequestMapping("/paymentmethods")
public class PaymentMethodController {

  @Autowired
  private PaymentMethodService paymentMethodService;

}
