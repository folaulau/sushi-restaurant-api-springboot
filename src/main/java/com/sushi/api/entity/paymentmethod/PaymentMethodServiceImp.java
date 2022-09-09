package com.sushi.api.entity.paymentmethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodServiceImp implements PaymentMethodService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
}
